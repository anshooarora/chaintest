package com.aventstack.chaintest.api.build;

import com.aventstack.chaintest.api.project.Project;
import com.aventstack.chaintest.api.project.ProjectService;
import com.aventstack.chaintest.api.runstats.RunStatsService;
import com.aventstack.chaintest.api.tag.TagService;
import com.aventstack.chaintest.api.tagstats.TagStatsService;
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

import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BuildService {

    private final BuildRepository repository;
    private final TagService tagService;
    private final RunStatsService runStatsService;
    private final TagStatsService tagStatsService;
    private final ProjectService projectService;

    @Autowired
    public BuildService(final BuildRepository repository, final TagService tagService,
                        final RunStatsService runStatsService, final TagStatsService tagStatsService,
                        final ProjectService projectService) {
        this.repository = repository;
        this.tagService = tagService;
        this.runStatsService = runStatsService;
        this.tagStatsService = tagStatsService;
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
                .orElseThrow(() -> new BuildNotFoundException("Build ID " + id + " was not found"));
    }

    @Transactional
    @CacheEvict(value = "builds", allEntries = true)
    @CachePut(value = "build", key = "#build.id")
    public Build create(final Build build) {
        tagService.associateTagsIfPresent(build);
        runStatsService.assignBuildInfo(build, null);
        tagStatsService.assignBuildInfo(build, null);
        if (build.getProjectId() > 0) {
            projectService.findById(build.getProjectId());
        } else if (null != build.getProjectName() && !build.getProjectName().isBlank()) {
            final Optional<Project> container = projectService.findByName(build.getProjectName());
            container.ifPresentOrElse(
                    p -> build.setProjectId(p.getId()),
                    () -> {
                        final Project project = new Project();
                        project.setName(build.getProjectName());
                        projectService.create(project);
                        build.setProjectId(project.getId());
                    });
        }
        return repository.save(build);
    }

    @Transactional
    @CacheEvict(value = "builds", allEntries = true)
    @CachePut(value = "build", key = "#build.id")
    public Build update(final Build build) {
        log.info("Saving build " + build);
        tagService.associateTagsIfPresent(build);
        final Optional<Build> findResult = repository.findById(build.getId());
        findResult.ifPresentOrElse(
                persisted -> {
                    runStatsService.assignBuildInfo(build, persisted);
                    tagStatsService.assignBuildInfo(build, persisted);
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
        log.info("Deleting build with id " + id);
        repository.deleteById(id);
        log.info("Build id: " + id + " was deleted successfully");
    }

}
