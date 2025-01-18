package com.aventstack.chainlp.api.build;

import com.aventstack.chainlp.api.build.SystemInfo.SystemInfoService;
import com.aventstack.chainlp.api.project.Project;
import com.aventstack.chainlp.api.project.ProjectNotSpecifiedException;
import com.aventstack.chainlp.api.project.ProjectService;
import com.aventstack.chainlp.api.buildstats.BuildStats;
import com.aventstack.chainlp.api.buildstats.BuildStatsService;
import com.aventstack.chainlp.api.tag.Tag;
import com.aventstack.chainlp.api.tagstats.TagStats;
import com.aventstack.chainlp.api.tagstats.TagStatsService;
import com.aventstack.chainlp.api.test.TestService;
import com.aventstack.chainlp.api.test.TestStatView;
import com.aventstack.chainlp.api.test.TestRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class BuildService {

    private final BuildRepository repository;
    private final ProjectService projectService;
    private final BuildStatsService buildStatsService;
    private final TagStatsService tagStatsService;
    private final TestRepository testRepository;
    private final SystemInfoService systemInfoService;
    private final TestService testService;

    public BuildService(final BuildRepository repository,
                        final ProjectService projectService,
                        final BuildStatsService buildStatsService,
                        final TagStatsService tagStatsService,
                        final TestRepository testRepository,
                        final SystemInfoService systemInfoService,
                        @Lazy final TestService testService) {
        this.repository = repository;
        this.projectService = projectService;
        this.buildStatsService = buildStatsService;
        this.tagStatsService = tagStatsService;
        this.testRepository = testRepository;
        this.systemInfoService = systemInfoService;
        this.testService = testService;
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Cacheable(value = "builds", unless = "#result == null || #result.totalElements == 0")
    public Page<Build> findAll(final long id, final long displayId, final Integer projectId, final String result,
                               final Long startedAfter, final Long endedBefore, final Pageable pageable) {
        final BuildSpec spec = new BuildSpec(
                Build.builder().id(id)
                        .displayId(displayId)
                        .projectId(projectId)
                        .result(result)
                        .startedAt(startedAfter)
                        .endedAt(endedBefore)
                        .build());
        return repository.findAll(spec, pageable);
    }

    @Cacheable(value = "build", key = "#id", unless = "#result == null")
    public Build findById(final long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BuildNotFoundException("Build with ID " + id + " was not found"));
    }

    @Transactional
    public Build create(final Build build) {
        log.info("Creating new build {}", build);
        findAndAssociateProject(build);
        final Optional<Build> lastBuild = repository.findFirstByProjectIdOrderByIdDesc(build.getProjectId());
        lastBuild.ifPresent(last -> {
            if (null != last.getDisplayId() && last.getDisplayId() > 0L) {
                build.setDisplayId(last.getDisplayId() + 1);
            }
        });
        if (null == build.getDisplayId() || 0L == build.getDisplayId()) {
            build.setDisplayId(1L);
        }
        final Build persisted = repository.save(build);
        if (null != build.getSystemInfo()) {
            build.getSystemInfo().forEach(x -> x.setBuild(persisted));
            systemInfoService.saveAll(build.getSystemInfo());
        }
        return persisted;
    }

    private void findAndAssociateProject(final Build build) {
        // if a project-id is specified on creation time, check if the project exists
        // this will throw a ProjectNotFoundException if project is not found
        if (null != build.getProjectId() && build.getProjectId() > 0) {
            projectService.findById(build.getProjectId());
        } else {
            // if a project-id was not specified, check if projectName is present and prevent
            // from persisting the build if a projectName was not provided
            if (StringUtils.isBlank(build.getProjectName())) {
                throw new ProjectNotSpecifiedException("Trying to save build without an associated project");
            }
            final Optional<Project> entity = projectService.findByName(build.getProjectName());
            // check if project with name exists, else, create
            entity.ifPresentOrElse(
                    project -> build.setProjectId(project.getId()),
                    () -> Optional.of(projectService.create(new Project(build.getProjectName())))
                            .ifPresent(project -> build.setProjectId(project.getId())));
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "builds", allEntries = true)
    @CachePut(value = "build", key = "#build.id")
    public Build update(final Build build) {
        log.info("Updating build {}", build);
        final Optional<Build> foundBuild = repository.findById(build.getId());
        foundBuild.ifPresentOrElse(
                persisted -> {
                    build.setDisplayId(persisted.getDisplayId());
                    updateBuild(build);
                },
                () -> { throw new BuildNotFoundException("Build with ID " + build.getId() + " was not found"); }
        );
        return build;
    }

    private void updateBuild(final Build build) {
        if (null == build.getBuildstats() || null == build.getTagStats()) {
            final List<TestStatView> projection = testRepository.findAllByBuildId(build.getId());
            if (!projection.isEmpty()) {
                if (null == build.getBuildstats()) {
                    updateRunStats(build, projection);
                }
                if (null == build.getTagStats()) {
                    updateTagStats(build, projection);
                }
            }
        }
        updateSystemInfo(build);
        repository.save(build);
    }

    private void updateRunStats(final Build build, final List<TestStatView> view) {
        if (null != build.getBuildstats()) {
            return;
        }
        buildStatsService.deleteForBuild(build.getId());
        final Set<BuildStats> stats = new HashSet<>();
        for (final TestStatView test : view) {
            final BuildStats stat = stats.stream().filter(x -> Objects.equals(x.getDepth(), test.getDepth()))
                    .findAny().orElseGet(() -> {
                        final BuildStats rs = BuildStats.builder().build(build).depth(test.getDepth()).build();
                        stats.add(rs);
                        return rs;
                    });
            stat.update(test.getResult());
        }
        build.setBuildstats(stats);
    }

    private void updateTagStats(final Build build, final List<TestStatView> view) {
        if (null != build.getTagStats()) {
            return;
        }
        tagStatsService.deleteForBuildId(build.getId());
        final Map<String, TagStats> stats = new HashMap<>();
        for (final TestStatView test : view) {
            for (final Tag tag : test.getTags()) {
                final String key = tag.getName() + "-" + test.getDepth();
                TagStats tagStat = stats.get(key);
                if (tagStat == null) {
                    tagStat = TagStats.builder()
                            .build(build)
                            .depth(test.getDepth())
                            .name(tag.getName())
                            .durationMs(test.getDurationMs())
                            .build();
                    stats.put(key, tagStat);
                }
                tagStat.update(test);
            }
        }
        build.setTagStats(new HashSet<>(stats.values()));
    }

    private void updateSystemInfo(final Build build) {
        if (null == build.getSystemInfo()) {
            return;
        }
        systemInfoService.deleteForBuildId(build.getId());
        build.getSystemInfo().forEach(x -> x.setBuild(build));
        systemInfoService.saveAll(build.getSystemInfo());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "builds", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "build", key = "#id")
    })
    public void delete(final long id) {
        log.debug("Deleting all tests for build with id {}", id);
        testService.deleteForBuild(id);
        log.info("Tests for build with id {} were deleted", id);
        log.debug("Deleting build with id {}", id);
        repository.deleteById(id);
        log.info("Build id: {} was deleted successfully", id);
    }

}
