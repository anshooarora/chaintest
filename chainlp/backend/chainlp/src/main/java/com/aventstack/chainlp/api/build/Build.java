package com.aventstack.chainlp.api.build;

import com.aventstack.chainlp.api.build.SystemInfo.SystemInfo;
import com.aventstack.chainlp.api.buildstats.BuildStats;
import com.aventstack.chainlp.api.tag.Tag;
import com.aventstack.chainlp.api.tagstats.TagStats;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "build")
public class Build {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "project_id")
    private Integer projectId;

    @Column(name = "name")
    private String name;

    @Column(name = "number")
    private Integer number;

    @Column(name = "project_name")
    private String projectName;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL)
    private Set<BuildStats> buildstats;

    @OneToMany(mappedBy = "build", cascade = CascadeType.ALL)
    private Set<TagStats> tagStats;

    @OneToMany(mappedBy = "build", cascade = CascadeType.REMOVE)
    private Set<SystemInfo> systemInfo;

    @Column(name = "started", nullable = false)
    private Long startedAt = System.currentTimeMillis();

    @Column(name = "ended")
    private Long endedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column(name = "execution_stage")
    private String executionStage;

    @Column(name = "testrunner")
    private String testRunner;

    @Column
    private String result;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "build_tag_rel",
            joinColumns = { @JoinColumn(name = "build") },
            inverseJoinColumns = { @JoinColumn(name = "tag") }
    )
    private Set<Tag> tags = new HashSet<>();

    @Column
    private boolean bdd;

}
