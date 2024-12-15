package com.aventstack.chainlp.api.buildstats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class BuildStatsService {

    @Autowired
    private BuildStatsRepository repository;

    public Optional<BuildStats> find(final Long buildId) {
        return repository.findByBuildId(buildId);
    }

    @Cacheable(value = "stat", key = "#id", unless = "#result == null && #result.isEmpty()")
    public Optional<BuildStats> findById(final Long id) {
        return repository.findById(id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "stat", key = "#runStat.id")
    public BuildStats create(final BuildStats stats) {
        return repository.save(stats);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "stat", key = "#stats.id")
    public BuildStats update(final BuildStats stats) {
        log.info("Updating run stats: {}", stats);
        repository.save(stats);
        return stats;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CacheEvict(value = "stat", allEntries = true)
    public void updateAll(final Set<BuildStats> stats) {
        log.info("Updating multiple run stats: {}", stats);
        repository.saveAll(stats);
    }

    @CacheEvict(value = "stat", key = "#id")
    public void delete(final Long id) {
        log.info("Deleting BuildStats with ID {}", id);
        repository.deleteById(id);
        log.info("BuildStats with ID {} deleted", id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CacheEvict(value = "stat", allEntries = true)
    public void deleteForBuild(final Long builId) {
        log.info("Deleting BuildStats with buildId {}", builId);
        repository.deleteByBuildId(builId);
        log.info("BuildStats for buildId {} deleted", builId);
    }

}