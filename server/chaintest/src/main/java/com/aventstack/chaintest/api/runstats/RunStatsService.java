package com.aventstack.chaintest.api.runstats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class RunStatsService {

    @Autowired
    private RunStatsRepository repository;

    public Optional<RunStats> find(final long buildId) {
        return repository.findByBuildId(buildId);
    }

    @Cacheable(value = "runStat", key = "#id")
    public Optional<RunStats> findById(final long id) {
        return repository.findById(id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "runStat", key = "#runStat.id")
    public RunStats create(final RunStats stats) {
        return repository.save(stats);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "runStat", key = "#stats.id")
    public RunStats update(final RunStats stats) {
        log.info("Updating run stats: {}", stats);
        repository.save(stats);
        return stats;
    }

    @CacheEvict(value = "runStat", key = "#id")
    public void delete(final long id) {
        log.info("Deleting RunStats with ID {}", id);
        repository.deleteById(id);
        log.info("RunStats with ID {} deleted", id);
    }

}
