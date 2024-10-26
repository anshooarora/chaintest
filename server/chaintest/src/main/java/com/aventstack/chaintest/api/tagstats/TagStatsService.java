package com.aventstack.chaintest.api.tagstats;

import com.aventstack.chaintest.api.build.Build;
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
public class TagStatsService {

    @Autowired
    private TagStatsRepository repository;

    public Optional<TagStats> find(final long buildId) {
        return repository.findByBuildId(buildId);
    }

    @Cacheable(value = "tagStat", key = "#id")
    public Optional<TagStats> findById(final long id) {
        return repository.findById(id);
    }

    @Transactional
    @CachePut(value = "tagStat", key = "#tagStat.id")
    public TagStats create(final TagStats stats) {
        return repository.save(stats);
    }

    @Transactional
    @CachePut(value = "tagStat", key = "#tagStat.id")
    public TagStats update(final TagStats stats) {
        log.info("Updating TagStats: " + stats);
        repository.findById(stats.getId()).ifPresentOrElse(
                x -> repository.save(stats),
                () -> {
                    throw new TagStatsNotFoundException("TagStats with ID " + stats.getId() + " was not found");
                }
        );
        return stats;
    }

    @Transactional
    @CacheEvict(value = "tagStat", key = "#id")
    public void delete(final long id) {
        log.info("Deleting TagStats with ID " + id);
        repository.deleteById(id);
        log.info("TagStats with ID " + id + " deleted");
    }

    public void assignBuildInfo(final Build httpRequestBody, final Build persisted) {
        if (null != httpRequestBody.getTagStats()) {
            for (final TagStats stats : httpRequestBody.getTagStats()) {
                if (null != persisted && null != persisted.getTagStats()) {
                    httpRequestBody.getTagStats().forEach(t -> {
                        t.setBuild(httpRequestBody);
                        persisted.getTagStats().stream().filter(p -> p.getName().equals(t.getName()))
                                .findAny().ifPresent(p -> t.setId(p.getId()));
                    });
                }
                stats.setBuild(httpRequestBody);
            }
        }
    }

}
