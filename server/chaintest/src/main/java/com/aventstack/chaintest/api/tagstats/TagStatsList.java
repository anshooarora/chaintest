package com.aventstack.chaintest.api.tagstats;

import com.aventstack.chaintest.api.build.Build;
import com.aventstack.chaintest.api.tag.Tag;
import com.aventstack.chaintest.api.test.Test;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
@ToString(exclude = "build")
@Entity
@Table(name = "tag_stats_list")
public class TagStatsList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "build_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Build build;

    private int depth;

    @OneToMany(mappedBy = "tagStatsList", cascade = CascadeType.ALL)
    private List<TagStats> tagStats = Collections.synchronizedList(new ArrayList<>());;

    private final Map<String, TagStats> map = new ConcurrentHashMap<>();

    public TagStatsList() { }

    public TagStatsList(final Build build, final int depth) {
        this.build = build;
        this.depth = depth;
    }

    public void update(final Test test) {
        if (null != test.getTags()) {
            for (final Tag tag : test.getTags()) {
                if (!map.containsKey(tag.getName())) {
                    final TagStats ts = new TagStats(this, tag.getName(), depth);
                    map.put(tag.getName(), ts);
                    tagStats.add(ts);
                }
                final TagStats stats = map.get(tag.getName());
                stats.update(test);
            }
        }
    }
}
