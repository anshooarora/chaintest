package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.ExceptionsUtil;
import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import java.util.stream.Stream;

/**
 * Represents a test in the ChainTest framework.
 * This class contains information about the test such as its name, description,
 * class name, start and end times, result, tags, error details, and child tests.
 * It also provides methods to complete the test and update its statistics.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Test implements ChainTestEntity {

    private long id;
    private long buildId;
    private long projectId;
    private UUID clientId = UUID.randomUUID();
    private String externalId;
    private String name;
    private String description;
    private String className;
    private long startedAt = System.currentTimeMillis();
    private long endedAt;
    private long durationMs;
    private String result = Result.PASSED.getResult();
    private Set<Tag> tags;
    private String error;
    @JsonIgnore
    private Test parent;
    private volatile List<Test> children = Collections.synchronizedList(new ArrayList<>());
    private List<String> logs = Collections.synchronizedList(new ArrayList<>());
    private List<Embed> embeds = new ArrayList<>();
    private int depth;
    private boolean isBDD;

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
        if (null != tags) {
            addTags(tags);
        }
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
        if (null != tags) {
            tags.forEach(this::addTag);
        }
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

    // Getters and setters for the fields

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getBuildId() {
        return buildId;
    }

    public void setBuildId(long buildId) {
        this.buildId = buildId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public Long getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Long endedAt) {
        this.endedAt = endedAt;
        setDurationMs(endedAt - startedAt);
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public String getDurationPretty() {
        return TimeUtil.getPrettyTime(getDurationMs());
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTags(Collection<String> tags) {
        if (null == this.tags) {
            this.tags = new HashSet<>();
        }
        tags.stream().map(Tag::new)
                .forEach(this.tags::add);
    }

    public void addTag(String tag) {
        if (null == this.tags) {
            this.tags = new HashSet<>();
        }
        this.tags.add(new Tag(tag));
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Test getParent() {
        return parent;
    }

    public void setParent(final Test test) {
        this.parent = test;
    }

    public List<Test> getChildren() {
        return children;
    }

    public void setChildren(List<Test> children) {
        this.children = children;
    }

    public void addChild(final Test child) {
        child.setParent(this);
        child.setDepth(depth + 1);
        children.add(child);
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public void addLog(final String log) {
        logs.add(log);
    }

    public boolean isBDD() {
        return isBDD;
    }

    public void setBDD(boolean BDD) {
        isBDD = BDD;
        children.forEach(x -> x.setBDD(BDD));
    }

    public List<Embed> getEmbeds() {
        return embeds;
    }

    public void setEmbeds(List<Embed> embeds) {
        this.embeds = embeds;
    }

    public void addEmbed(final String base64, final String mediaType) {
        embeds.add(new Embed(base64, mediaType));
    }

    public void addEmbed(final File file, final String mediaType) {
        embeds.add(new Embed(file, mediaType));
    }

    public void addEmbed(final byte[] bytes, final String mediaType) {
        embeds.add(new Embed(bytes, mediaType));
    }

    public void addEmbed(final Embed embed) {
        embeds.add(embed);
    }

}
