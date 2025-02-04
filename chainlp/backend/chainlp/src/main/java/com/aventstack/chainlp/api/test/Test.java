package com.aventstack.chainlp.api.test;

import com.aventstack.chainlp.api.tag.Tag;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "parent", "embeds" })
@Table(name = "test")
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "client_id", unique = true)
    private String clientId;

    @Column(name = "build_id")
    private Long buildId;

    @Column(name = "build_display_id")
    private Long buildDisplayId;

    public void setBuildDisplayId(final Long buildDisplayId) {
        this.buildDisplayId = buildDisplayId;
        if (children != null) {
            children.forEach(x -> x.setBuildDisplayId(buildDisplayId));
        }
    }

    @Column(name = "project_id")
    private Integer projectId;

    public void setProjectId(final Integer projectId) {
        this.projectId = projectId;
        if (children != null) {
            children.forEach(x -> x.setProjectId(projectId));
        }
    }

    @CreatedDate
    @Column(name = "started", nullable = false)
    private Long startedAt = System.currentTimeMillis();

    @Column(name = "ended")
    private Long endedAt;

    @Column(name = "duration_ms")
    private Long durationMs;

    @Column
    private String className;

    @Column
    @NotBlank(message = "Missing mandatory field 'name'")
    private String name;

    @Column
    private String description;

    @Column
    private Short depth;

    @Column
    private String result;

    @Column
    private boolean bdd;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "test_tag_rel",
            joinColumns = { @JoinColumn(name = "test") },
            inverseJoinColumns = { @JoinColumn(name = "tag") }
    )
    private Set<Tag> tags = new HashSet<>();

    @ManyToOne
    @JsonIgnore
    private Test parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Test> children;

    public void setChildren(List<Test> children) {
        this.children = children;
        if (children != null) {
            children.forEach(x -> x.setParent(this));
        }
    }

    @Column(columnDefinition = "TEXT")
    private String error;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Embed> embeds;

    public void setEmbeds(final List<Embed> embeds) {
        this.embeds = embeds;
        if (embeds != null) {
            for (final Embed embed : embeds) {
                embed.setTest(this);
                if (embed.isStore()) {
                    if (null != embed.getBase64() && !embed.getBase64().isEmpty()) {
                        final byte[] data = Base64.getDecoder().decode(embed.getBase64().getBytes());
                        embed.setBytes(data);
                        embed.setBase64(null);
                    }
                } else {
                    embed.setBase64(null);
                    embed.setBytes(null);
                }
            }
        }
    }

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL)
    private List<Log> logs;

    public void setLogs(final List<Log> logs) {
        if (logs == null || logs.isEmpty()) {
            return;
        }
        this.logs = logs;
        logs.forEach(x -> x.setTest(this));
    }

}
