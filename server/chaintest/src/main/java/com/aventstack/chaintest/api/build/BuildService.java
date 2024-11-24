package com.aventstack.chaintest.api.build;

import com.aventstack.chaintest.api.project.Project;
import com.aventstack.chaintest.api.project.ProjectService;
import com.aventstack.chaintest.api.runstats.RunStats;
import com.aventstack.chaintest.api.tag.TagService;
import com.aventstack.chaintest.api.tagstats.TagStatsList;
import com.aventstack.chaintest.api.test.Test;
import com.aventstack.chaintest.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BuildService {

    private static final Map<Long, Map<Integer, RunStats>> RUN_STATS = new ConcurrentHashMap<>();
    private static final Map<Long, Map<Integer, TagStatsList>> TAG_STATS = new ConcurrentHashMap<>();

    private final BuildRepository repository;
    private final TagService tagService;
    private final ProjectService projectService;

    @Autowired
    public BuildService(final BuildRepository repository,
                        final TagService tagService,
                        final ProjectService projectService) {
        this.repository = repository;
        this.tagService = tagService;
        this.projectService = projectService;
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
        if (build.getProjectId() > 0) {
            projectService.findById(build.getProjectId());
        } else if (null != build.getProjectName() && !build.getProjectName().isBlank()) {
            final Optional<Project> container = projectService.findByName(build.getProjectName());
            container.ifPresentOrElse(
                    p -> build.setProjectId(p.getId()),
                    () -> {
                        Assert.notNullOrEmpty(build.getProjectName(),
                                "Failed to save build. Project name cannot be null or empty");
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
        final Map<Integer, RunStats> stats = new ConcurrentHashMap<>();
        RUN_STATS.put(build.getId(), stats);
        final Map<Integer, TagStatsList> tagStats = new ConcurrentHashMap<>();
        TAG_STATS.put(build.getId(), tagStats);
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
        final Map<Integer, TagStatsList> stats = TAG_STATS.get(test.getBuildId());
        if (!stats.containsKey(test.getDepth())) {
            stats.put(test.getDepth(), new TagStatsList(test.getBuild(), test.getDepth()));
        }
        stats.get(test.getDepth()).update(test);
        if (null != test.getChildren()) {
            for (final Test node : test.getChildren()) {
                updateTagStats(node);
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
                    build.setRunStats(RUN_STATS.get(build.getId()).values());
                    build.setTagStats(TAG_STATS.get(build.getId()));
                    repository.save(build);
                },
                () -> { throw new BuildNotFoundException("Build with ID " + build.getId() + " was not found"); }
        );
        return build;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "builds", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "build", key = "#id", condition="#id > 0")
    })
    public void delete(final long id) {
        log.info("Deleting build with id {}", id);
        repository.deleteById(id);
        log.info("Build id: {} was deleted successfully", id);
    }

}
