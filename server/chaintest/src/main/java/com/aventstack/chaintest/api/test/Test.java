package com.aventstack.chaintest.api.test;

import com.aventstack.chaintest.api.build.Build;
import com.aventstack.chaintest.api.domain.Taggable;
import com.aventstack.chaintest.api.tag.Tag;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Set;

@Data
@ToString(exclude = "build")
@Entity
@Table(name = "test")
public class Test implements Taggable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "build", insertable = false, updatable = false)
    @JsonBackReference
    private Build build;

    @Column(name = "build")
    private long buildId;

    @CreatedDate
    @Column(name = "started", nullable = false)
    private long startedAt = System.currentTimeMillis();

    @Column(name = "ended")
    private long endedAt;

    @Column(name = "duration_ms")
    private long durationMs;

    @Column
    private String ancestor;

    @Column
    private String parent;

    @Column
    @NotBlank(message = "Missing mandatory field 'name'")
    private String name;

    @Column
    private int depth;

    @Column
    private String result;

    @ManyToMany
    @JoinTable(
            name = "test_tag_rel",
            joinColumns = { @JoinColumn(name = "test") },
            inverseJoinColumns = { @JoinColumn(name = "tag") }
    )
    private Set<Tag> tags;

    @OneToMany(mappedBy = "tests", fetch = FetchType.LAZY)
    private List<Test> tests;

    @Column(columnDefinition = "TEXT")
    private String error;

}
