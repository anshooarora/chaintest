package com.aventstack.chaintest.api.project;

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
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Cacheable(value = "projects", unless = "#result == null || #result.size == 0")
    public Page<Project> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "project", key = "#id")
    public Optional<Project> findById(final int id) {
        return repository.findById(id);
    }

    @Cacheable(value = "project", key = "#name")
    public Optional<Project> findByName(final String name) {
        return repository.findByName(name);
    }

    @Transactional
    @CacheEvict(value = "projects", allEntries = true)
    @CachePut(value = "project", key = "#project.id")
    public Project create(final Project project) {
        log.debug("Saving project {}", project);
        return repository.save(project);
    }

    @Transactional
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
    public void delete(final int id) {
        log.info("Deleting project with id {}", id);
        repository.deleteById(id);
        log.info("Project id: {} was deleted successfully", id);
    }

}
