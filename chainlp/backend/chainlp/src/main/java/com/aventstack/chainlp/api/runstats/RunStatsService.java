package com.aventstack.chainlp.api.runstats;

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
public class RunStatsService {

    @Autowired
    private RunStatsRepository repository;

    public Optional<RunStats> find(final Long buildId) {
        return repository.findByBuildId(buildId);
    }

    @Cacheable(value = "stat", key = "#id", unless = "#result == null && #result.isEmpty()")
    public Optional<RunStats> findById(final Long id) {
        return repository.findById(id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "stat", key = "#runStat.id")
    public RunStats create(final RunStats stats) {
        return repository.save(stats);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "stat", key = "#stats.id")
    public RunStats update(final RunStats stats) {
        log.info("Updating run stats: {}", stats);
        repository.save(stats);
        return stats;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CacheEvict(value = "stat", allEntries = true)
    public void updateAll(final Set<RunStats> stats) {
        log.info("Updating multiple run stats: {}", stats);
        repository.saveAll(stats);
    }

    @CacheEvict(value = "stat", key = "#id")
    public void delete(final Long id) {
        log.info("Deleting RunStats with ID {}", id);
        repository.deleteById(id);
        log.info("RunStats with ID {} deleted", id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CacheEvict(value = "stat", allEntries = true)
    public void deleteForBuild(final Long builId) {
        log.info("Deleting RunStats with buildId {}", builId);
        repository.deleteByBuildId(builId);
        log.info("RunStats for buildId {} deleted", builId);
    }

}