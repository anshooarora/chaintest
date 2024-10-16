package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Build implements ChainTestEntity {

    private long id;
    private String workspace;
    private long startedAt = System.currentTimeMillis();
    private long endedAt;
    private long durationMs;
    private String testRunner;
    private String name;
    private String result;
    private Stats stats;
    private Set<Tag> tags;
    private String gitRepository;
    private String gitBranch;
    private String gitCommitHash;
    private String gitTags;
    private String gitCommitMessage;

    public Build() { }

    public Build(final String testRunner) {
        this.testRunner = testRunner;
    }

    public void complete(final Result result) {
        complete();
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

    public String getWorkspace() {
        return workspace;
    }

    public void setWorkspace(String workspace) {
        this.workspace = workspace;
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

    public String getTestRunner() {
        return testRunner;
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

    public String getGitRepository() {
        return gitRepository;
    }

    public void setGitRepository(String gitRepository) {
        this.gitRepository = gitRepository;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getGitCommitHash() {
        return gitCommitHash;
    }

    public void setGitCommitHash(String gitCommitHash) {
        this.gitCommitHash = gitCommitHash;
    }

    public String getGitTags() {
        return gitTags;
    }

    public void setGitTags(String gitTags) {
        this.gitTags = gitTags;
    }

    public String getGitCommitMessage() {
        return gitCommitMessage;
    }

    public void setGitCommitMessage(String gitCommitMessage) {
        this.gitCommitMessage = gitCommitMessage;
    }
}
