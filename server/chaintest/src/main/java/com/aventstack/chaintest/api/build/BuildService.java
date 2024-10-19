package com.aventstack.chaintest.api.build;

import com.aventstack.chaintest.api.runstats.RunStatsService;
import com.aventstack.chaintest.api.tag.TagService;
import com.aventstack.chaintest.api.tagstats.TagStatsService;
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
public class BuildService {

    @Autowired
    private BuildRepository repository;

    @Autowired
    private TagService tagService;

    @Autowired
    private RunStatsService runStatsService;

    @Autowired
    private TagStatsService tagStatsService;

    @Cacheable(value = "builds", unless = "#result == null || #result.size == 0")
    public Page<Build> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "build", key = "#id")
    public Build findById(final long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BuildNotFoundException("Build ID " + id + " was not found"));
    }

    @Transactional
    @CacheEvict(value = "builds", allEntries = true)
    @CachePut(value = "build", key = "#build.id")
    public Build create(final Build build) {
        tagService.associateTagsIfPresent(build);
        return repository.save(build);
    }

    @Transactional
    @CacheEvict(value = "builds", allEntries = true)
    @CachePut(value = "build", key = "#build.id")
    public Build update(final Build build) {
        log.info("Saving build " + build);
        tagService.associateTagsIfPresent(build);
        repository.findById(build.getId()).ifPresentOrElse(
                x -> repository.save(build),
                () -> { throw new BuildNotFoundException("Build with ID " + build.getId() + " was not found"); }
        );
        return build;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "builds", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "build", key = "#id", condition="#id > 0")
    })
    public void delete(final long id) {
        log.info("Deleting build with id " + id);
        repository.deleteById(id);
        log.info("Build id: " + id + " was deleted successfully");
    }

}
