package com.aventstack.chaintest.api.tagstats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@Slf4j
public class TagStatsService {

    @Autowired
    private TagStatsRepository repository;

    @Cacheable(value = "tagStat", key = "#id")
    public Optional<TagStats> findById(final long id) {
        return repository.findById(id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "tagStat", key = "#tagStat.id")
    public TagStats create(final TagStats stats) {
        return repository.save(stats);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @CachePut(value = "tagStat", key = "#stats.id")
    public TagStats update(final TagStats stats) {
        log.info("Updating TagStats: {}", stats);
        return repository.save(stats);
    }

    @Transactional
    @CacheEvict(value = "tagStat", key = "#id")
    public void delete(final long id) {
        log.info("Deleting TagStats with ID {}", id);
        repository.deleteById(id);
        log.info("TagStats with ID {} deleted", id);
    }

}
