package com.aventstack.chaintest.api.runstats;

import com.aventstack.chaintest.api.build.Build;
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

    @Transactional
    @CachePut(value = "runStat", key = "#runStat.id")
    public RunStats create(final RunStats stats) {
        if (0L == stats.getBuildId()) {
            throw new MissingBuildPropertyException("Missing buildId for RunStats");
        }
        return repository.save(stats);
    }

    @Transactional
    @CachePut(value = "runStat", key = "#runStat.id")
    public RunStats update(final RunStats stats) {
        log.info("Updating run stats: " + stats);
        repository.findById(stats.getId()).ifPresentOrElse(
                x -> repository.save(stats),
                () -> {
                    throw new RunStatsNotFoundException("RunStats with ID " + stats.getId() + " was not found");
                }
        );
        return stats;
    }

    @Transactional
    @CacheEvict(value = "runStat", key = "#id")
    public void delete(final long id) {
        log.info("Deleting RunStats with ID " + id);
        repository.deleteById(id);
        log.info("RunStats with ID " + id + " deleted");
    }

    public void associateRunStats(final Build build) {
        if (null != build.getRunStats()) {
            build.getRunStats().setBuild(build);
            if (build.getRunStats().getId() == 0) {
                create(build.getRunStats());
            } else {
                update(build.getRunStats());
            }
        }
    }

}
