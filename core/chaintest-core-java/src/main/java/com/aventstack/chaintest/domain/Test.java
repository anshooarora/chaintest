package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.ExceptionsUtil;
import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a test in the ChainTest framework.
 * This class contains information about the test such as its name, description,
 * class name, start and end times, result, tags, error details, and child tests.
 * It also provides methods to complete the test and update its statistics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class Test implements ChainTestEntity {

    /**
     * The unique identifier for the test.
     */
    private long id;

    /**
     * The identifier for the build associated with the test.
     */
    private long buildId;

    /**
     * The identifier for the project associated with the test.
     */
    private long projectId;

    /**
     * The unique client identifier for the test.
     */
    private final UUID clientId = UUID.randomUUID();

    /**
     * The external identifier for the test.
     */
    private String externalId;

    /**
     * The name of the test.
     */
    private String name;

    /**
     * The description of the test.
     */
    private String description;

    /**
     * The class name associated with the test.
     */
    private String className;

    /**
     * The start time of the test in milliseconds.
     */
    private long startedAt = System.currentTimeMillis();

    /**
     * The end time of the test in milliseconds.
     */
    private long endedAt;

    /**
     * The duration of the test in milliseconds.
     */
    private long durationMs;

    /**
     * The result of the test.
     */
    private String result = Result.PASSED.getResult();

    /**
     * The tags associated with the test.
     */
    private final Set<Tag> tags = new HashSet<>();

    /**
     * The error details if the test failed.
     */
    private String error;

    /**
     * The child tests of this test.
     */
    private volatile List<Test> children = Collections.synchronizedList(new ArrayList<>());

    /**
     * The logs associated with the test.
     */
    private final List<String> logs = Collections.synchronizedList(new ArrayList<>());

    /**
     * The embedded media associated with the test.
     */
    private final List<Embed> embeds = new ArrayList<>();

    /**
     * The depth of the test in the test hierarchy.
     */
    private int depth;

    /**
     * Indicates if the test is a BDD (Behavior-Driven Development) test.
     */
    private boolean isBDD;

    @JsonIgnore
    private Test parent;

    /**
     * Default constructor.
     */
    public Test() { }

    /**
     * Constructs a Test with the specified build ID, name, optional class name, and tags.
     *
     * @param buildId the build ID
     * @param name the name of the test
     * @param testClass the optional class name of the test
     * @param tags the tags associated with the test
     */
    public Test(final long buildId, final String name, final Optional<String> testClass, final Collection<String> tags) {
        setBuildId(buildId);
        setName(name);
        addTags(tags);
        testClass.ifPresent(Test.this::setClassName);
    }

    /**
     * Constructs a Test with the specified build ID, name, optional class name, and tags.
     *
     * @param buildId the build ID
     * @param name the name of the test
     * @param testClass the optional class name of the test
     * @param tags the tags associated with the test
     */
    public Test(final long buildId, final String name, final Optional<String> testClass, final Stream<String> tags) {
        setBuildId(buildId);
        setName(name);
        tags.forEach(this::addTag);
        testClass.ifPresent(Test.this::setClassName);
    }

    /**
     * Constructs a Test with the specified name, optional class name, and tags.
     *
     * @param name the name of the test
     * @param testClass the optional class name of the test
     * @param tags the tags associated with the test
     */
    public Test(final String name, final Optional<String> testClass, final Collection<String> tags) {
        this(0L, name, testClass, tags);
    }

    /**
     * Constructs a Test with the specified name, optional class name, and tags.
     *
     * @param name the name of the test
     * @param testClass the optional class name of the test
     * @param tags the tags associated with the test
     */
    public Test(final String name, final Optional<String> testClass, final Stream<String> tags) {
        this(0L, name, testClass, tags);
    }

    /**
     * Constructs a Test with the specified build ID, name, and optional class name.
     *
     * @param buildId the build ID
     * @param name the name of the test
     * @param testClass the optional class name of the test
     */
    public Test(final long buildId, final String name, final Optional<String> testClass) {
        this(buildId, name, testClass, Stream.empty());
    }

    /**
     * Constructs a Test with the specified name and optional class name.
     *
     * @param name the name of the test
     * @param testClass the optional class name of the test
     */
    public Test(final String name, final Optional<String> testClass) {
        this(0L, name, testClass);
    }

    /**
     * Constructs a Test with the specified build ID, name, and tags.
     *
     * @param buildId the build ID
     * @param name the name of the test
     * @param tags the tags associated with the test
     */
    public Test(final long buildId, final String name, final Collection<String> tags) {
        this(buildId, name, Optional.empty(), tags);
    }

    /**
     * Constructs a Test with the specified build ID, name, and tags.
     *
     * @param buildId the build ID
     * @param name the name of the test
     * @param tags the tags associated with the test
     */
    public Test(final long buildId, final String name, final Stream<String> tags) {
        this(buildId, name, Optional.empty(), tags);
    }

    /**
     * Constructs a Test with the specified name and tags.
     *
     * @param name the name of the test
     * @param tags the tags associated with the test
     */
    public Test(final String name, final Collection<String> tags) {
        this(0L, name, tags);
    }

    /**
     * Constructs a Test with the specified name and tags.
     *
     * @param name the name of the test
     * @param tags the tags associated with the test
     */
    public Test(final String name, final Stream<String> tags) {
        this(0L, name, tags);
    }

    /**
     * Constructs a Test with the specified build ID and name.
     *
     * @param buildId the build ID
     * @param name the name of the test
     */
    public Test(final long buildId, final String name) {
        this(buildId, name, Stream.empty());
    }

    /**
     * Constructs a Test with the specified name.
     *
     * @param name the name of the test
     */
    public Test(final String name) {
        setName(name);
    }

    /**
     * Constructs a Test with the specified build ID, method, and tags.
     *
     * @param buildId the build ID
     * @param method the method representing the test
     * @param tags the tags associated with the test
     */
    public Test(final long buildId, final Method method, final Collection<String> tags) {
        this(buildId, method.getName(), tags);
    }

    /**
     * Constructs a Test with the specified method and tags.
     *
     * @param method the method representing the test
     * @param tags the tags associated with the test
     */
    public Test(final Method method, final Collection<String> tags) {
        this(0L, method, tags);
    }

    /**
     * Constructs a Test with the specified build ID and method.
     *
     * @param buildId the build ID
     * @param method the method representing the test
     */
    public Test(final long buildId, final Method method) {
        this(buildId, method.getName());
    }

    /**
     * Constructs a Test with the specified method.
     *
     * @param method the method representing the test
     */
    public Test(final Method method) {
        this(0L, method);
    }

    /**
     * Completes the test with the current result.
     */
    public void complete() {
        complete(Optional.empty());
    }

    /**
     * Completes the test with the specified error.
     *
     * @param error the optional error that occurred during the test
     */
    public void complete(final Optional<Throwable> error) {
        setEndedAt(System.currentTimeMillis());
        error.ifPresent(x -> {
            setError(ExceptionsUtil.readStackTrace(x));
            setResult(Result.FAILED.getResult());
        });
        if (null != parent) {
            final Result result = Result.computePriority(Result.valueOf(getResult()), Result.valueOf(parent.getResult()));
            parent.setResult(result.getResult());
            parent.complete();
        }
    }

    /**
     * Completes the test with the specified error.
     *
     * @param error the error that occurred during the test
     */
    public void complete(final Throwable error) {
        complete(Optional.ofNullable(error));
    }

    public void setEndedAt(final Long endedAt) {
        this.endedAt = endedAt;
        setDurationMs(endedAt - startedAt);
    }

    /**
     * Returns the duration of the test in a human-readable format.
     *
     * @return the duration of the test as a pretty string
     */
    public String getDurationPretty() {
        return TimeUtil.getPrettyTime(getDurationMs());
    }

    public void addTags(final Collection<String> tags) {
        if (null != tags) {
            tags.forEach(this::addTag);
        }
    }

    public void addTag(final String tag) {
        if (null != tag && !tag.isBlank()) {
            final Tag t = new Tag(tag);
            this.tags.add(t);
            if (null != parent) {
                parent.addTag(t);
            }
        }
    }

    public void addTag(final Tag tag) {
        if (null != tag) {
            this.tags.add(tag);
            if (null != parent) {
                parent.addTag(tag);
            }
        }
    }

    public void addChild(final Test child) {
        child.setParent(this);
        child.setDepth(depth + 1);
        child.getTags().forEach(this::addTag);
        children.add(child);
    }

    /**
     * Adds a log entry to the test.
     *
     * @param log the log entry to add
     */
    public void addLog(final String log) {
        logs.add(log);
    }

    /**
     * Sets whether the test is a BDD (Behavior-Driven Development) test.
     *
     * @param isBDD true if the test is a BDD test, false otherwise
     */
    public void setIsBdd(final boolean isBDD) {
        this.isBDD = isBDD;
        children.forEach(x -> x.setIsBdd(isBDD));
    }

    /**
     * Adds an embedded media to the test.
     *
     * @param base64 the base64-encoded media
     * @param mediaType the media type
     */
    public void addEmbed(final String base64, final String mediaType) {
        embeds.add(new Embed(base64, mediaType));
    }

    /**
     * Adds an embedded media to the test from a file.
     *
     * @param file the file containing the media
     * @param mediaType the media type
     */
    public void addEmbed(final File file, final String mediaType) {
        embeds.add(new Embed(file, mediaType));
    }

    /**
     * Adds an embedded media to the test from a byte array.
     *
     * @param bytes the byte array containing the media
     * @param mediaType the media type
     */
    public void addEmbed(final byte[] bytes, final String mediaType) {
        embeds.add(new Embed(bytes, mediaType));
    }

    /**
     * Adds an embedded media to the test.
     *
     * @param embed the embedded media to add
     */
    public void addEmbed(final Embed embed) {
        embeds.add(embed);
    }

}
