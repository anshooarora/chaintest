package com.aventstack.chaintest.api.build;

import com.aventstack.chaintest.api.domain.Taggable;
import com.aventstack.chaintest.api.runstats.RunStats;
import com.aventstack.chaintest.api.tag.Tag;
import com.aventstack.chaintest.api.tagstats.TagStatsList;
import com.aventstack.chaintest.api.test.Test;
import com.aventstack.chaintest.api.tagstats.TagStats;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@ToString(exclude = "tests")
@Entity
@Table(name = "build")
public class Build implements Taggable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "project_id")
    private int projectId;

    @Transient
    private String projectName;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL)
    private Collection<RunStats> runStats;

    // mapped by depth -> list of tag-stats
    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL)
    private Map<Integer, TagStatsList> tagStats;

    @Column(name = "started", nullable = false)
    private long startedAt;

    @Column(name = "ended")
    private long endedAt;

    @Column(name = "duration")
    private long durationMs;

    @Column(name = "execution_stage")
    private String executionStage;

    @Column(name = "test_runner")
    private String testRunner;

    @Column
    private String name;

    @Column
    private String result;

    @OneToMany(mappedBy = "id")
    @JsonIgnore
    private List<Test> tests;

    @ManyToMany
    @JoinTable(
            name = "build_tag_rel",
            joinColumns = { @JoinColumn(name = "build") },
            inverseJoinColumns = { @JoinColumn(name = "tag") }
    )
    private Set<Tag> tags;

    @Column(name = "git_repo")
    private String gitRepository;

    @Column(name = "git_branch")
    private String gitBranch;

    @Column(name = "git_commit_hash")
    private String gitCommitHash;

    @Column(name = "git_tags")
    private String gitTags;

    @Column(name = "git_commit_message")
    private String gitCommitMessage;

    @Column(name = "is_bdd")
    private Boolean bdd;

    public Build() { }

    public Build(final Test test) {
        startedAt = test.getStartedAt();
        tags = test.getTags();
        result = test.getResult();
    }

}
