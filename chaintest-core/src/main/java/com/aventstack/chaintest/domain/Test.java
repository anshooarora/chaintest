package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.ExceptionUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Test() { }

    public Test(final long buildId, final String name, final Optional<Class<?>> testClass, final Set<String> tags) {
        setBuildId(buildId);
        setName(name);
        addTags(tags);
        testClass.ifPresent(x -> setClassName(x.getName()));
    }

    public Test(final long buildId, final Method method) {
        this.buildId = buildId;
        this.name = method.getName();
        this.className = method.getDeclaringClass().getName();
    }

    public void complete() {
        setEndedAt(System.currentTimeMillis());
    }

    public void complete(final Optional<Throwable> error) {
        setEndedAt(System.currentTimeMillis());
        error.ifPresent(x -> {
            this.error = ExceptionUtil.readStackTrace(x);
            this.result = Result.FAILED.getResult();
        });
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

    public void addTags(Set<String> tags) {
        if (null == this.tags) {
            this.tags = new HashSet<>();
        }
        final List<Tag> t = tags.stream().map(Tag::new)
                .collect(Collectors.toUnmodifiableList());
        this.tags.addAll(t);
    }

    public void addTags(List<String> tags) {
        if (null == this.tags) {
            this.tags = new HashSet<>();
        }
        final List<Tag> t = tags.stream().map(Tag::new)
                .collect(Collectors.toUnmodifiableList());
        this.tags.addAll(t);
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
