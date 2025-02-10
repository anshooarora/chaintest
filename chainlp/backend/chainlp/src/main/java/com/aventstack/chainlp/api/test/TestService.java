package com.aventstack.chainlp.api.test;

import com.aventstack.chainlp.api.build.Build;
import com.aventstack.chainlp.api.build.BuildService;
import com.aventstack.chainlp.api.project.ProjectService;
import com.aventstack.chainlp.api.tag.Tag;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        if (null != test.getTags() && !test.getTags().isEmpty() && !page.isEmpty()) {
            filterChildrenForTags(page.getContent(), test.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));
        }
        if (null != test.getChildren() && !test.getChildren().isEmpty() && !page.isEmpty()) {
            final short depth = getDepth(test, (short) 0);
            filterChildrenForResult(page.getContent(), test.getChildren().iterator().next(), depth);
        }
        for (final Test t : page.getContent()) {
            resolveEmbeds(t);
        }
        return page;
    }

    private Test resolveEmbeds(final Test test) {
        if (test.getEmbeds().stream().anyMatch(x -> null != x.getUrl() && !x.getUrl().isBlank())) {
            for (final Embed embed : test.getEmbeds()) {
                final String presigned = embedResolver.getResolver(embed.getUrl())
                        .resolve(embed.getUrl());
                embed.setPresigned(presigned);
            }
        }
        test.getChildren().forEach(this::resolveEmbeds);
        return test;
    }

    private void filterChildrenForTags(final List<Test> tests, final Set<String> tags) {
        for (final Test t : tests) {
            t.setChildren(t.getChildren().stream()
                    .filter(x -> x.getTags().stream().anyMatch(y -> tags.contains(y.getName())))
                    .toList());
            if (!t.isBdd()) {
                filterChildrenForTags(t.getChildren(), tags);
            }
        }
    }

    private void filterChildrenForResult(final List<Test> tests, final Test filter, final short depth) {
        for (final Test t : tests) {
            if (depth == 1) {
                t.setChildren(t.getChildren().stream()
                        .filter(x -> x.getResult().equals(filter.getResult()))
                        .toList());
            }
            if (depth == 2) {
                final List<Test> remove = new ArrayList<>();
                for (final Test child : t.getChildren()) {
                    child.setChildren(child.getChildren().stream()
                            .filter(x -> x.getResult().equals(filter.getChildren().iterator().next().getResult()))
                            .toList());
                    if (child.getChildren().isEmpty()) {
                        remove.add(child);
                    }
                }
                for (final Test r : remove) {
                    t.getChildren().remove(r);
                }
            }
        }
    }

    public short getDepth(final Test test, final short startingDepth) {
        short depth = startingDepth;
        Test currentTest = test;
        while (currentTest.getChildren() != null && !currentTest.getChildren().isEmpty()) {
            currentTest = currentTest.getChildren().iterator().next();
            depth++;
        }
        return depth;
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
            final Build build = buildService.findById(test.getBuildId());
            final Integer projectId = build.getProjectId();
            test.setProjectId(projectId);
            test.setBuildDisplayId(build.getDisplayId());
        } else {
            // else, we will check if the project exists
            projectService.findById(test.getProjectId());
        }

        if (null == test.getBuildDisplayId() || 0L == test.getBuildDisplayId()) {
            final Build build = buildService.findById(test.getBuildId());
            test.setBuildDisplayId(build.getDisplayId());
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

    public void clearParentRefs(final Test test) {
        if (null != test.getChildren()) {
            test.getChildren().forEach(child -> {
                child.setParent(null);
                if (null != child.getChildren()) {
                    child.getChildren().forEach(leaf -> leaf.setParent(null));
                }
            });
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Caching(evict = {
            @CacheEvict(value = "tests", allEntries = true, condition = "#id > 0"),
            @CacheEvict(value = "test", allEntries = true)
    })
    public void deleteForProject(final Integer id) {
        log.debug("Deleting all tests for project with id {}", id);
        repository.deleteByProjectId(id);
        log.info("Builds for tests with id {} were deleted", id);
    }

}
