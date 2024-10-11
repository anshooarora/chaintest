package com.aventstack.chaintest.api.workspace;

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

@Service
@Slf4j
@Transactional(readOnly = true)
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository repository;

    @Cacheable(value = "workspaces", unless = "#result == null || #result.size == 0")
    public Page<Workspace> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "workspace", key = "#id")
    public Workspace findById(final int id) {
        return repository.findById(id)
                .orElseThrow(() -> new WorkspaceNotFoundException("Not found"));
    }

    @Transactional
    @CacheEvict(value = "workspaces", allEntries = true)
    @CachePut(value = "workspace", key = "#workspace.id")
    public Workspace create(final Workspace workspace) {
        log.debug("Saving workspace " + workspace);
        return repository.save(workspace);
    }

    @Transactional
    @CacheEvict(value = "workspaces", allEntries = true)
    @CachePut(value = "workspace", key = "#workspace.id")
    public Workspace update(final Workspace workspace) {
        log.info("Saving workspace " + workspace);
        repository.findById(workspace.getId()).ifPresentOrElse(
                x -> repository.save(workspace),
                () -> { throw new WorkspaceNotFoundException("Workspace with ID " + workspace.getId() + " was not found"); }
        );
        return workspace;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "workspaces", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "workspace", key = "#id", condition="#id > 0")
    })
    public void delete(final int id) {
        log.info("Deleting workspace with id " + id);
        repository.deleteById(id);
        log.info("Workspace id: " + id + " was deleted successfully");
    }

}
