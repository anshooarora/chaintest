package com.aventstack.chaintest.api.test;

import com.aventstack.chaintest.api.build.BuildService;
import com.aventstack.chaintest.api.tag.TagService;
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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TestService {

    private static final ConcurrentHashMap<UUID, Long> BUILD_INFO = new ConcurrentHashMap<>();

    @Autowired
    private TestRepository repository;

    @Autowired
    private TagService tagService;

    @Autowired
    private BuildService buildService;

    @Cacheable(value = "tests", unless = "#result == null || #result.size == 0")
    public Page<Test> findAll(final Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "test", key = "#id")
    public Test findById(final long id) {
        return repository.findById(id)
                .orElseThrow(() -> new TestNotFoundException("Not found"));
    }

    @Transactional
    @CacheEvict(value = "tests", allEntries = true)
    @CachePut(value = "test", key = "#test.id")
    public Test create(final Test test) {
        if (0L == test.getBuildId()) {
            throw new MissingBuildPropertyException("Mandatory field [buildId] was not provided for this test");
        }
        tagService.associateTagsIfPresent(test);
        log.debug("Saving test " + test + " for buildId: " + test.getBuildId());
        return repository.save(test);
    }

    @Transactional
    @CacheEvict(value = "tests", allEntries = true)
    @CachePut(value = "test", key = "#test.id")
    public Test update(final Test test) {
        log.info("Saving test " + test);
        repository.findById(test.getId()).ifPresentOrElse(
                x -> repository.save(test),
                () -> { throw new TestNotFoundException("Test with ID " + test.getId() + " was not found"); }
        );
        return test;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tests", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "test", key = "#id", condition="#id > 0")
    })
    public void delete(final long id) {
        log.info("Deleting test with id " + id);
        repository.deleteById(id);
        log.info("Test id: " + id + " was deleted successfully");
    }

}
