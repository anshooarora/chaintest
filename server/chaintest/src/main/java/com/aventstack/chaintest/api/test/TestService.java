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

import java.util.Set;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TestService {

    private final TestRepository repository;
    private final TagService tagService;
    private final BuildService buildService;

    @Autowired
    public TestService(final TestRepository repository,
                       final TagService tagService,
                       final BuildService buildService) {
        this.repository = repository;
        this.tagService = tagService;
        this.buildService = buildService;
    }

    @Cacheable(value = "tests", unless = "#result == null || #result.size == 0")
    public Page<Test> findAll(final String name, final Long buildId, final Integer depth, final String result,
                              final Set<String> tags, final String error, final Pageable pageable) {
        final Test test = new Test();
        test.setName(name);
        test.setBuildId(buildId == null ? 0 : buildId);
        test.setResult(result);
        if (null != depth) {
            test.setDepth(depth);
        }
        if (null != tags) {
            test.setTag(tags);
        }
        test.setError(error);
        final TestSpec spec = new TestSpec(test);
        return repository.findAll(spec, pageable);
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
        tagService.createAssignTags(test);
        test.addChildRel();
        buildService.updateStatsForTest(test);
        log.debug("Saving test {} for buildId: {}", test, test.getBuildId());
        return repository.save(test);
    }

    @Transactional
    @CacheEvict(value = "tests", allEntries = true)
    @CachePut(value = "test", key = "#test.id")
    public Test update(final Test test) {
        log.info("Saving test {}", test);
        repository.findById(test.getId())
                .ifPresentOrElse(
                x -> {
                    buildService.updateStatsForTest(test);
                    repository.save(test);
                },
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
        log.info("Deleting test with id {}", id);
        repository.deleteById(id);
        log.info("Test id: {} was deleted successfully", id);
    }

    public void deleteForBuild(final long buildId) {
        log.info("Deleting all tests for build-id {}", buildId);
        repository.deleteByBuildId(buildId);
        log.info("Tests removed");
    }

}
