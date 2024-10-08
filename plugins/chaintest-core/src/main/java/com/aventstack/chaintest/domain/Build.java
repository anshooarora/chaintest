package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Build implements ChainTestEntity {

    private long id;
    private long startedAt = System.currentTimeMillis();
    private long endedAt;
    private long durationMs;
    private String name;
    private String result;
    private Stats stats;
    private Set<Tag> tags;
    private String branch;
    private String commitHash;
    private String gitTag;
    private String commitMessage;

    public void complete(final Result result) {
        setEndedAt(System.currentTimeMillis());
        setResult(result.getResult());
    }

    public void complete() {
        setEndedAt(System.currentTimeMillis());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    @JsonIgnore
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public void setCommitHash(String commitHash) {
        this.commitHash = commitHash;
    }

    @JsonIgnore
    public String getGitTag() {
        return gitTag;
    }

    public void setGitTag(String gitTag) {
        this.gitTag = gitTag;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }
}
