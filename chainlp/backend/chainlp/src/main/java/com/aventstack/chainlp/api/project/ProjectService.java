package com.aventstack.chainlp.api.project;

import com.aventstack.chainlp.api.build.BuildService;
import com.aventstack.chainlp.api.test.TestService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Autowired
    private BuildService buildService;

    @Autowired
    @Lazy
    private TestService testService;

    @Cacheable(value = "projects", unless = "#result == null || #result.size == 0")
    public Page<Project> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "project", key = "#id")
    public Project findById(final Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + id + " was not found"));
    }

    @Cacheable(value = "project", key = "#name", unless = "#result == null")
    public Optional<Project> findByName(final String name) {
        return repository.findByName(name);
    }

    @CacheEvict(value = "projects", allEntries = true)
    @CachePut(value = "project", key = "#project.id", unless = "#result == null")
    public Project create(final Project project) {
        log.debug("Saving project {}", project);
        return repository.save(project);
    }

    @CacheEvict(value = "projects", allEntries = true)
    @CachePut(value = "project", key = "#project.id")
    public Project update(final Project project) {
        log.info("Saving project {}", project);
        repository.findById(project.getId()).ifPresentOrElse(
                x -> repository.save(project),
                () -> { throw new ProjectNotFoundException("Project with ID " + project.getId() + " was not found"); }
        );
        return project;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "projects", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "project", key = "#id", condition="#id > 0")
    })
    public void delete(final Integer id) {
        buildService.deleteForProject(id);
        testService.deleteForProject(id);
        log.info("Deleting project with id {}", id);
        repository.deleteById(id);
        log.info("Project id: {} was deleted successfully", id);
    }

}
