package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * Represents a build in the ChainTest framework.
 * This class contains information about the build such as project details,
 * execution stage, test runner, git information, and statistics.
 * It also provides methods to update and complete the build.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Build implements ChainTestEntity {

    private long id;
    private int projectId;
    private String projectName;
    private long startedAt = System.currentTimeMillis();
    private long endedAt;
    private long durationMs;
    private ExecutionStage executionStage = ExecutionStage.IN_PROGRESS;
    private String testRunner;
    private String name;
    private String result = Result.PASSED.getResult();
    @JsonIgnore
    private final Queue<RunStats> runStats = new ConcurrentLinkedQueue<>();
    @JsonIgnore
    private final Set<TagStats> tagStats = ConcurrentHashMap.newKeySet();
    private final Map<String, TagStats> tagStatsMonitor = new ConcurrentHashMap<>();
    private Set<Tag> tags = ConcurrentHashMap.newKeySet();
    private String gitRepository;
    private String gitBranch;
    private String gitCommitHash;
    private String gitTags;
    private String gitCommitMessage;
    private boolean isBDD;
    private List<SystemInfo> systemInfo;

    /**
     * Default constructor.
     */
    public Build() {
    }

    /**
     * Constructs a Build with the specified project ID and test runner.
     *
     * @param projectId  the project ID
     * @param testRunner the test runner
     */
    public Build(final int projectId, final String testRunner) {
        this.projectId = projectId;
        this.testRunner = testRunner;
    }

    /**
     * Constructs a Build with the specified project name and test runner.
     *
     * @param projectName the project name
     * @param testRunner  the test runner
     */
    public Build(final String projectName, final String testRunner) {
        this.projectName = projectName;
        this.testRunner = testRunner;
    }

    /**
     * Constructs a Build with the specified test runner.
     *
     * @param testRunner the test runner
     */
    public Build(final String testRunner) {
        this.testRunner = testRunner;
    }

    /**
     * Updates the statistics of the build based on the provided test.
     *
     * @param test the test to update statistics from
     */
    public void updateStats(final Test test) {
        setEndedAt(System.currentTimeMillis());
        setResult(Result.computePriority(getResult(), test.getResult()).getResult());
        updateRunStats(test);
        updateTagStats(test);
        test.setIsBdd(isBDD());
    }

    private synchronized void updateRunStats(final Test test) {
        final RunStats stat = runStats.stream()
                .filter(x -> x.getDepth() == test.getDepth())
                .findAny()
                .orElseGet(() -> addRunStatsDepth(test.getDepth()));
        stat.update(test);
        for (final Test t : test.getChildren()) {
            updateRunStats(t);
        }
    }

    private RunStats addRunStatsDepth(final int depth) {
        final RunStats stat = new RunStats(depth);
        runStats.add(stat);
        return stat;
    }

    private void updateTagStats(final Test test) {
        if (null != test.getTags()) {
            addTags(test.getTags());
            for (final Tag tag : test.getTags()) {
                if (!tagStatsMonitor.containsKey(tag.getName() + test.getDepth())) {
                    final TagStats ts = new TagStats(test.getDepth());
                    ts.setName(tag.getName());
                    tagStats.add(ts);
                    tagStatsMonitor.put(tag.getName() + test.getDepth(), ts);
                }
                tagStatsMonitor.get(tag.getName() + test.getDepth()).update(test);
                for (final Test child : test.getChildren()) {
                    updateTagStats(child);
                }
            }
        }
    }

    /**
     * Completes the build with the specified result.
     *
     * @param result the result to set
     */
    public void complete(final Result result) {
        setEndedAt(System.currentTimeMillis());
        setResult(result.getResult());
    }

    /**
     * Completes the build with the current result.
     */
    public void complete() {
        complete(Result.valueOf(result));
    }

    public void setEndedAt(final long endedAt) {
        this.endedAt = endedAt;
        setDurationMs(endedAt - startedAt);
    }

    public String getDurationPretty() {
        return TimeUtil.getPrettyTime(getDurationMs());
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

    public void addTags(final Collection<Tag> tags) {
        if (null != tags && !tags.isEmpty()) {
            this.tags.addAll(tags);
        }
    }

    public void addTags(final Stream<Tag> tags) {
        if (null != tags) {
            tags.forEach(this.tags::add);
        }
    }

    public void setIsBdd(boolean val) {
        isBDD = val;
    }

}
