package com.aventstack.chaintest.api.build;

import com.aventstack.chaintest.api.domain.Taggable;
import com.aventstack.chaintest.api.stats.Stats;
import com.aventstack.chaintest.api.tag.Tag;
import com.aventstack.chaintest.api.test.Test;
import com.aventstack.chaintest.api.workspace.Workspace;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Set;

@Data
@ToString(exclude = "tests")
@Entity
@Table(name = "build")
public class Build implements Taggable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "workspace_id", insertable = false)
    @JsonBackReference
    private Workspace workspace;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "build", referencedColumnName = "id")
    private Stats stats;

    @Column(name = "started", nullable = false)
    private long startedAt;

    @Column(name = "ended")
    private long endedAt;

    @Column(name = "duration")
    private long durationMs;

    @Column(name = "testrunner")
    private String testRunner;

    @Column
    private String name;

    @Column
    private String result;

    @OneToMany(mappedBy = "build")
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

    public Build() { }

    public Build(final Test test) {
        startedAt = test.getStartedAt();
        tags = test.getTags();
        result = test.getResult();
    }

}
