package com.aventstack.chaintest.api.test;

import com.aventstack.chaintest.api.build.Build;
import com.aventstack.chaintest.api.domain.Taggable;
import com.aventstack.chaintest.api.tag.Tag;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Set;

@Data
@ToString(exclude = { "build", "parent" })
@Entity
@Table(name = "test")
public class Test implements Taggable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "build_id", insertable = false, updatable = false)
    @JsonBackReference
    private Build build;

    @Column(name = "build_id")
    private long buildId;

    @CreatedDate
    @Column(name = "started", nullable = false)
    private long startedAt = System.currentTimeMillis();

    @Column(name = "ended")
    private long endedAt;

    @Column(name = "duration_ms")
    private long durationMs;

    @Column
    private String packageName;

    @Column
    private String className;

    @Column
    @NotBlank(message = "Missing mandatory field 'name'")
    private String name;

    @Column
    private Integer depth;

    @Column
    private String result;

    @ManyToMany
    @JoinTable(
            name = "test_tag_rel",
            joinColumns = { @JoinColumn(name = "test") },
            inverseJoinColumns = { @JoinColumn(name = "tag") }
    )
    private Set<Tag> tags;

    @JsonIgnore
    private transient Set<String> tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @JsonIgnore
    private Test parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Collection<Test> children;

    @Column(columnDefinition = "TEXT")
    private String error;

    public void addChildRel() {
        if (null != getChildren() && !getChildren().isEmpty()) {
            for (final Test child : getChildren()) {
                child.setParent(this);
                child.setDepth(depth + 1);
                child.addChildRel();
            }
        }
    }

}
