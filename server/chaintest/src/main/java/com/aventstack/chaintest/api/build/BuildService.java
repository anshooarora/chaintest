package com.aventstack.chaintest.api.build;

import com.aventstack.chaintest.api.project.Project;
import com.aventstack.chaintest.api.project.ProjectNotSpecifiedException;
import com.aventstack.chaintest.api.project.ProjectService;
import com.aventstack.chaintest.api.runstats.RunStats;
import com.aventstack.chaintest.api.runstats.RunStatsService;
import com.aventstack.chaintest.api.tag.Tag;
import com.aventstack.chaintest.api.tag.TagService;
import com.aventstack.chaintest.api.tagstats.TagStats;
import com.aventstack.chaintest.api.tagstats.TagStatsService;
import com.aventstack.chaintest.api.test.Test;
import com.aventstack.chaintest.api.test.TestService;
import com.aventstack.chaintest.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BuildService {

    private static final Map<Long, Map<Integer, RunStats>> RUN_STATS = new ConcurrentHashMap<>();
    private static final Map<Long, Map<Integer, List<TagStats>>> TAG_STATS = new ConcurrentHashMap<>();
    private static final Object tagStatCreationLock = new Object();

    private final BuildRepository repository;
    private final TagService tagService;
    private final ProjectService projectService;
    private final RunStatsService runStatsService;
    private final TagStatsService tagStatsService;
    private final TestService testService;

    @Autowired
    public BuildService(final BuildRepository repository,
                        final TagService tagService,
                        final ProjectService projectService,
                        final RunStatsService runStatsService,
                        final TagStatsService tagStatsService,
                        @Lazy final TestService testService) {
        this.repository = repository;
        this.tagService = tagService;
        this.projectService = projectService;
        this.runStatsService = runStatsService;
        this.tagStatsService = tagStatsService;
        this.testService = testService;
    }

    @Cacheable(value = "builds", unless = "#result == null || #result.size == 0")
    public Page<Build> findAll(final int projectId, final Pageable pageable) {
        final Build build = new Build();
        build.setProjectId(projectId);
        final BuildSpec spec = new BuildSpec(build);
        return repository.findAll(spec, pageable);
    }

    @Cacheable(value = "build", key = "#id")
    public Build findById(final long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BuildNotFoundException("Build with ID " + id + " was not found"));
    }

    @Transactional
    @CacheEvict(value = "builds", allEntries = true)
    @CachePut(value = "build", key = "#build.id")
    public Build create(final Build build) {
        log.info("Creating new build {}", build);
        tagService.createAssignTags(build);

        // if a project-id is specified on creation time, check if the project exists
        // this will throw a ProjectNotFoundException if project is not found
        if (build.getProjectId() > 0) {
            projectService.findById(build.getProjectId());
        } else {
            // if a project-id was not specified, check if projectName is present and prevent
            // from persisting the build if a projectName was not provided
            if (StringUtils.isBlank(build.getProjectName())) {
                throw new ProjectNotSpecifiedException("Trying to save build without specifying a project name");
            }
            final Optional<Project> container = projectService.findByName(build.getProjectName());
            // check if project with name exists, else, create
            container.ifPresentOrElse(
                    p -> build.setProjectId(p.getId()),
                    () -> {
                        final Project project = new Project();
                        project.setName(build.getProjectName());
                        projectService.create(project);
                        build.setProjectId(project.getId());
                    });
        }

        final Build persisted = repository.save(build);
        initStatsTracker(persisted);
        return persisted;
    }

    private void initStatsTracker(final Build build) {
        final Map<Integer, RunStats> map = new ConcurrentHashMap<>();
        map.put(0, new RunStats(build));
        RUN_STATS.put(build.getId(), map);
        TAG_STATS.put(build.getId(), new ConcurrentHashMap<>());
    }

    public void updateStatsForTest(final Test test) {
        Assert.notEqual(0, test.getBuildId(),
                "Attempting to save test with for a built not yet created");
        final Build build = findById(test.getBuildId());
        test.setBuild(build);
        updateRunStats(test);
        updateTagStats(test);
    }

    private void updateRunStats(final Test test) {
        final Map<Integer, RunStats> stats = RUN_STATS.get(test.getBuildId());
        if (!stats.containsKey(test.getDepth())) {
            stats.put(test.getDepth(), new RunStats(test.getBuild(), test.getDepth()));
        }
        stats.get(test.getDepth()).update(test);
        if (null != test.getChildren()) {
            for (final Test node : test.getChildren()) {
                node.setBuild(test.getBuild());
                updateRunStats(node);
            }
        }
    }

    private void updateTagStats(final Test test) {
        if (null == test.getTags() || test.getTags().isEmpty()) {
            return;
        }

        final Map<Integer, List<TagStats>> stats = TAG_STATS.get(test.getBuildId());
        if (!stats.containsKey(test.getDepth())) {
            stats.put(test.getDepth(), Collections.synchronizedList(new ArrayList<>()));
        }

        for (final Tag tag : test.getTags()) {
            TagStats tagstats;
            final Optional<TagStats> container = stats.get(test.getDepth()).stream()
                    .filter(x -> x.getName().equals(tag.getName()))
                    .findAny();
            if (container.isEmpty()) {
                synchronized (tagStatCreationLock) {
                    tagstats = stats.get(test.getDepth()).stream()
                            .filter(x -> x.getName().equals(tag.getName()))
                            .findAny()
                            .orElseGet(() -> {
                                final TagStats stat = new TagStats(test.getBuild(), tag.getName(), test.getDepth());
                                stats.get(test.getDepth()).add(stat);
                                return stat;
                            });
                }
            } else {
                tagstats = container.get();
            }

            tagstats.update(test);

            if (null != test.getChildren()) {
                for (final Test node : test.getChildren()) {
                    node.setBuild(test.getBuild());
                    updateTagStats(node);
                }
            }
        }
    }

    @Transactional
    @CacheEvict(value = "builds", allEntries = true)
    @CachePut(value = "build", key = "#build.id")
    public Build update(final Build build) {
        log.info("Saving build {}", build);
        tagService.createAssignTags(build);
        final Optional<Build> findResult = repository.findById(build.getId());
        findResult.ifPresentOrElse(
                persisted -> {
                    repository.save(build);
                    persistStats(build);
                },
                () -> {
                    throw new BuildNotFoundException("Build with ID " + build.getId() + " was not found");
                }
        );
        return build;
    }

    private void persistStats(final Build build) {
        final Map<Integer, RunStats> runStatsMap = RUN_STATS.get(build.getId());
        for (Map.Entry<Integer, RunStats> entry : runStatsMap.entrySet()) {
            runStatsService.update(entry.getValue());
        }

        final Map<Integer, List<TagStats>> tagStatsMap = TAG_STATS.get(build.getId());
        for (Map.Entry<Integer, List<TagStats>> entry : tagStatsMap.entrySet()) {
            final List<TagStats> list = entry.getValue();
            for (final TagStats stat : list) {
                tagStatsService.update(stat);
            }
        }

        // if executionStage == {FINISHED}, remove entry from collection
        if (build.getExecutionStage().equalsIgnoreCase("finished")) {
            RUN_STATS.remove(build.getId());
            TAG_STATS.remove(build.getId());
        }
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "builds", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "build", key = "#id", condition="#id > 0")
    })
    public void delete(final long id) {
        log.info("Deleting all tests for build with id {}", id);
        testService.deleteForBuild(id);
        log.info("Deleting build with id {}", id);
        repository.deleteById(id);
        log.info("Build id: {} was deleted successfully", id);
    }

}
