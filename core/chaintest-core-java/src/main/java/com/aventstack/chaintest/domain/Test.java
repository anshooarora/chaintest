package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.ExceptionsUtil;
import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Test implements ChainTestEntity {

    private long id;
    private long buildId;
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
    private int depth;
    private UUID clientId = UUID.randomUUID();
    private boolean isBDD;

    public Test() { }

    public Test(final long buildId, final String name, final Optional<String> testClass, final Collection<String> tags) {
        setBuildId(buildId);
        setName(name);
        if (null != tags) {
            addTags(tags);
        }
        testClass.ifPresent(Test.this::setClassName);
    }

    public Test(final long buildId, final String name, final Optional<String> testClass, final Stream<String> tags) {
        setBuildId(buildId);
        setName(name);
        if (null != tags) {
            tags.forEach(this::addTag);
        }
        testClass.ifPresent(Test.this::setClassName);
    }

    public Test(final String name, final Optional<String> testClass, final Collection<String> tags) {
        this(0L, name, testClass, tags);
    }

    public Test(final String name, final Optional<String> testClass, final Stream<String> tags) {
        this(0L, name, testClass, tags);
    }

    public Test(final long buildId, final String name, final Optional<String> testClass) {
        this(buildId, name, testClass, Stream.empty());
    }

    public Test(final String name, final Optional<String> testClass) {
        this(0L, name, testClass);
    }

    public Test(final long buildId, final String name, final Collection<String> tags) {
        this(buildId, name, Optional.empty(), tags);
    }

    public Test(final long buildId, final String name, final Stream<String> tags) {
        this(buildId, name, Optional.empty(), tags);
    }

    public Test(final String name, final Collection<String> tags) {
        this(0L, name, tags);
    }

    public Test(final String name, final Stream<String> tags) {
        this(0L, name, tags);
    }

    public Test(final long buildId, final String name) {
        this(buildId, name, Stream.empty());
    }

    public Test(final String name) {
        setName(name);
    }

    public Test(final long buildId, final Method method, final Collection<String> tags) {
        this(buildId, method.getName(), tags);
    }

    public Test(final Method method, final Collection<String> tags) {
        this(0L, method, tags);
    }

    public Test(final long buildId, final Method method) {
        this(buildId, method.getName());
    }

    public Test(final Method method) {
        this(0L, method);
    }

    public void complete() {
        complete(Optional.empty());
    }

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

    public void complete(final Throwable error) {
        complete(Optional.ofNullable(error));
    }

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

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public boolean isBDD() {
        return isBDD;
    }

    public void setBDD(boolean BDD) {
        isBDD = BDD;
        children.forEach(x -> x.setBDD(BDD));
    }

}
