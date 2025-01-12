package com.aventstack.chainlp.api.test;

import com.aventstack.chainlp.api.build.BuildService;
import com.aventstack.chainlp.api.project.ProjectService;
import com.aventstack.chainlp.embed.SignedEmbedResolverFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Slf4j
@Service
public class TestService {

    private static final Function<Long, String> TEST_NOT_FOUND = x -> "Test with ID " + x + " was not found";

    private final TestRepository repository;
    private final ProjectService projectService;
    private final BuildService buildService;
    private final SignedEmbedResolverFactory embedResolver;

    @Autowired
    public TestService(final TestRepository repository, final ProjectService projectService, final BuildService buildService,
                       final SignedEmbedResolverFactory embedResolver) {
        this.repository = repository;
        this.projectService = projectService;
        this.buildService = buildService;
        this.embedResolver = embedResolver;
    }

    @Transactional(readOnly = true, isolation = Isolation.READ_UNCOMMITTED)
    @Cacheable(value = "tests", unless = "#result == null")
    public Page<Test> findAll(final Test test, final String op, final Pageable pageable) {
        final Page<Test> page = repository.findAll(new TestSpec(test, op), pageable);
        for (final Test t : page.getContent()) {
            resolveEmbeds(t);
        }
        return page;
    }

    private Test resolveEmbeds(final Test test) {
        for (final String s : test.getScreenshotURL()) {
            test.getScreenshotURL().set(test.getScreenshotURL().indexOf(s),
                    embedResolver.getResolver(s).resolve(s));
        }
        test.getChildren().forEach(this::resolveEmbeds);
        return test;
    }

    @Cacheable(value = "test", key = "#id", unless = "#result == null")
    public Test findById(final long id) {
        final Test test = repository.findById(id)
                .orElseThrow(() -> new TestNotFoundException(TEST_NOT_FOUND.apply(id)));
        return resolveEmbeds(test);
    }

    @CacheEvict(value = "tests", allEntries = true, condition = "#result.id > 0")
    @CachePut(value = "test", key = "#test.id", unless = "#result == null")
    public Test create(final Test test) {
        log.debug("Creating new test {}", test);

        if (0L == test.getBuildId()) {
            throw new MissingBuildPropertyException("Mandatory field [buildId] was not provided for this test");
        }

        // if client does not provide a project-id, we will try to find the project-id from the build
        if (null == test.getProjectId() || 0L == test.getProjectId()) {
            final Integer projectId = buildService.findById(test.getBuildId()).getProjectId();
            test.setProjectId(projectId);
        } else {
            // else, we will check if the project exists
            projectService.findById(test.getProjectId());
        }

        test.getChildren().forEach(x -> x.setProjectId(test.getProjectId()));

        log.debug("Saving test {} for buildId: {}", test, test.getBuildId());
        try {
            return repository.save(test);
        } catch (final DataIntegrityViolationException e) {
            throw new DuplicateTestException("Test with ID " + test.getClientId() + " already exists");
        }
    }

    @CacheEvict(value = "tests", allEntries = true)
    @CachePut(value = "test", key = "#test.id", condition = "#test.id > 0")
    public Test update(final Test test) {
        log.info("Saving test {}", test);
        repository.findById(test.getId())
                .ifPresentOrElse(
                    x -> repository.save(test),
                    () -> { throw new TestNotFoundException(TEST_NOT_FOUND.apply(test.getId())); }
                );
        return test;
    }

    @Caching(evict = {
            @CacheEvict(value = "tests", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "test", key = "#id")
    })
    public void delete(final long id) {
        log.info("Deleting test with id {}", id);
        repository.deleteById(id);
        log.info("Test id: {} was deleted successfully", id);
    }

    @Transactional(propagation = Propagation.MANDATORY)
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
