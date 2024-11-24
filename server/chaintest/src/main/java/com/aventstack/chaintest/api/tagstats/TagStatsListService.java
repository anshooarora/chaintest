package com.aventstack.chaintest.api.tagstats;

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
public class TagStatsListService {

    @Autowired
    private TagStatsListRepository repository;

    @Cacheable(value = "tagStatList", key = "#id")
    public Optional<TagStatsList> findById(final long id) {
        return repository.findById(id);
    }

    @Transactional
    @CachePut(value = "tagStatList", key = "#tagStat.id")
    public TagStatsList create(final TagStatsList stats) {
        return repository.save(stats);
    }

    @Transactional
    @CachePut(value = "tagStat", key = "#tagStat.id")
    public TagStatsList update(final TagStatsList stats) {
        log.info("Updating TagStatsList: {}", stats);
        return repository.save(stats);
    }

    @Transactional
    @CacheEvict(value = "tagStatList", key = "#id")
    public void delete(final long id) {
        log.info("Deleting TagStats with ID {}", id);
        repository.deleteById(id);
        log.info("TagStats with ID {} deleted", id);
    }

}
