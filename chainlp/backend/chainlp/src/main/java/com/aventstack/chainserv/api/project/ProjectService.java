package com.aventstack.chainserv.api.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class ProjectService {

    @Autowired
    private ProjectRepository repository;

    @Cacheable(value = "projects", unless = "#result == null || #result.size == 0")
    public Page<Project> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "project", key = "#id", unless = "#result == null")
    public Project findById(final Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID " + id + " was not found"));
    }

    @Cacheable(value = "project", key = "#id", unless = "#result.isEmpty()")
    public Optional<Project> findByName(final String name) {
        return repository.findByName(name);
    }

    public Project create(final Project project) {
        log.debug("Saving project {}", project);
        return repository.save(project);
    }

}
