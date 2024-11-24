package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Build implements ChainTestEntity {

    private long id;
    private int projectId;
    private String projectName = "default";
    private long startedAt = System.currentTimeMillis();
    private long endedAt;
    private long durationMs;
    private ExecutionStage executionStage = ExecutionStage.IN_PROGRESS;
    private String testRunner;
    private String name;
    private String result = Result.PASSED.getResult();
    private Set<Tag> tags = ConcurrentHashMap.newKeySet();
    private String gitRepository;
    private String gitBranch;
    private String gitCommitHash;
    private String gitTags;
    private String gitCommitMessage;
    private boolean isBDD;

    public Build() { }

    public Build(final int projectId, final String testRunner) {
        this.projectId = projectId;
        this.testRunner = testRunner;
    }

    public Build(final String projectName, final String testRunner) {
        this.projectName = projectName;
        this.testRunner = testRunner;
    }

    public Build(final String testRunner) {
        this.testRunner = testRunner;
    }

    public void updateStats(final Test test) {
        setEndedAt(System.currentTimeMillis());
        setResult(Result.computePriority(getResult(), test.getResult()).getResult());
    }

    public void complete(final Result result) {
        setEndedAt(System.currentTimeMillis());
        setResult(result.getResult());
    }

    public void complete() {
        complete(Result.valueOf(result));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
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

    public ExecutionStage getExecutionStage() {
        return executionStage;
    }

    public void setExecutionStage(ExecutionStage executionStage) {
        this.executionStage = executionStage;
    }

    public String getTestRunner() {
        return testRunner;
    }

    public void setTestRunner(String testRunner) { this.testRunner = testRunner; }

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

    public void setEndedAt(long endedAt) {
        this.endedAt = endedAt;
        this.durationMs = endedAt - startedAt;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public void addTags(final List<String> tags) {
        if (null != tags) {
            final Stream<Tag> t = tags.stream().map(Tag::new);
            addTags(t);
        }
    }

    public void addTags(final Set<String> tags) {
        if (null != tags) {
            final Stream<Tag> t = tags.stream().map(Tag::new);
            addTags(t);
        }
    }

    public void addTags(Collection<Tag> tags) {
        if (null != tags && !tags.isEmpty()) {
            this.tags.addAll(tags);
        }
    }

    public void addTags(Stream<Tag> tags) {
        if (null != tags) {
            tags.forEach(this.tags::add);
        }
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

    public boolean isBDD() {
        return isBDD;
    }

    public void setBDD(boolean val) {
        isBDD = val;
    }
}
