package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Build implements ChainTestEntity {

    private long id;
    private String workspace;
    private long startedAt = System.currentTimeMillis();
    private long endedAt;
    private long durationMs;
    private ExecutionStage executionStage = ExecutionStage.IN_PROGRESS;
    private String testRunner;
    private String name;
    private String result = Result.PASSED.getResult();
    private final Set<RunStats> runStats = ConcurrentHashMap.newKeySet();
    private final Set<TagStats> tagStats = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, TagStats> tagStatsMonitor = new ConcurrentHashMap<>();
    private Set<Tag> tags = ConcurrentHashMap.newKeySet();
    private String gitRepository;
    private String gitBranch;
    private String gitCommitHash;
    private String gitTags;
    private String gitCommitMessage;

    public Build() { }

    public Build(final String testRunner) {
        this.testRunner = testRunner;
    }

    public void updateStats(final Test test) {
        final RunStats stat = runStats.stream()
                .filter(x -> x.getDepth() == test.getDepth())
                .findAny().orElseGet(() -> addRunStatsDepth(test.getDepth()));
        stat.update(test);

        if (null != test.getTags()) {
            for (final Tag tag : test.getTags()) {
                if (!tagStatsMonitor.containsKey(tag.getName())) {
                    final TagStats ts = new TagStats();
                    ts.setName(tag.getName());
                    tagStats.add(ts);
                    tagStatsMonitor.put(tag.getName(), ts);
                }
                tagStatsMonitor.get(tag.getName()).update(test);
            }
        }
    }

    private RunStats addRunStatsDepth(final int depth) {
        final RunStats stat = new RunStats(depth);
        runStats.add(stat);
        return stat;
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

    public Set<RunStats> getRunStats() {
        return runStats;
    }

    public Set<TagStats> getTagStats() {
        return tagStats;
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
        if (null != tags) {
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
}
