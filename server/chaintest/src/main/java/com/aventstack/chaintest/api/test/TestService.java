package com.aventstack.chaintest.api.test;

import com.aventstack.chaintest.api.build.BuildService;
import com.aventstack.chaintest.api.tag.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@Transactional(readOnly = true)
public class TestService {

    private final Set<String> clientId = ConcurrentHashMap.newKeySet();

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

    public Page<Test> findAll(final String name, final int projectId, final long buildId, final Integer depth, final String result,
                              final Set<String> tags, final String error, final String op, final Pageable pageable) {
        final Test test = new Test();
        test.setName(name);
        test.setProjectId(projectId);
        test.setBuildId(buildId);
        test.setResult(result);
        if (null != depth) {
            test.setDepth(depth);
        }
        if (null != tags) {
            test.setTag(tags);
        }
        test.setError(error);
        final Predicate.BooleanOperator operator = StringUtils.isBlank(op) || op.equalsIgnoreCase("and")
                ? Predicate.BooleanOperator.AND
                : Predicate.BooleanOperator.OR;
        final TestSpec spec = new TestSpec(test, operator);
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
        if (clientId.contains(test.getClientId())) {
            log.error("Attempt to save a duplicate test with clientId {}", test.getClientId());
            return repository.findByClientId(test.getClientId())
                    .orElseThrow(() -> new IllegalStateException("Unable to save and get existing test with clientId "
                            + test.getClientId()));
        }
        clientId.add(test.getClientId());

        if (0L == test.getBuildId()) {
            throw new MissingBuildPropertyException("Mandatory field [buildId] was not provided for this test");
        }

        if (0L == test.getProjectId()) {
            final int projectId = buildService.findById(test.getBuildId()).getProjectId();
            test.setProjectId(projectId);
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

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "tests", allEntries = true),
            @CacheEvict(value = "test", allEntries = true)
    })
    public void deleteForBuild(final long buildId) {
        log.info("Deleting all tests for build-id {}", buildId);
        repository.deleteByBuildId(buildId);
        log.info("Tests removed");
    }

}
