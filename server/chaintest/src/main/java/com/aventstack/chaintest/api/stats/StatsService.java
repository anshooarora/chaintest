package com.aventstack.chaintest.api.stats;

import com.aventstack.chaintest.api.test.MissingBuildPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class StatsService {

    @Autowired
    private StatsRepository repository;

    public Optional<Stats> find(final long buildId) {
        return repository.findByBuildId(buildId);
    }

    @Cacheable(value = "stat", key = "#id")
    public Optional<Stats> findById(final long id) {
        return repository.findById(id);
    }

    @Transactional
    @CachePut(value = "stat", key = "#stat.id")
    public Stats create(final Stats stats) {
        if (0L == stats.getBuildId()) {
            throw new MissingBuildPropertyException("Missing buildId for Stats");
        }
        return repository.save(stats);
    }

    @Transactional
    @CachePut(value = "stat", key = "#stat.id")
    public Stats update(final Stats stats) {
        log.info("Updating stats: " + stats);
        repository.findById(stats.getId()).ifPresentOrElse(
                x -> repository.save(stats),
                () -> {
                    throw new StatsNotFoundException("Stats with ID " + stats.getId() + " was not found");
                }
        );
        return stats;
    }

    @Transactional
    @CacheEvict(value = "stat", key = "#id")
    public void delete(final long id) {
        log.info("Deleting Stats with ID " + id);
        repository.deleteById(id);
        log.info("Stats with ID " + id + " deleted");
    }

}
