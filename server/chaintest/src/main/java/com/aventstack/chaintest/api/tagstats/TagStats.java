package com.aventstack.chaintest.api.tagstats;

import com.aventstack.chaintest.api.test.Test;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@ToString(exclude = "tagStatsList")
@Entity
@EqualsAndHashCode
@DiscriminatorOptions(force = true)
@Table(name = "tag_stats")
public class TagStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "tag_stats_list")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private TagStatsList tagStatsList;

    private int depth;
    private String name;
    private int total;
    private int passed;
    private int failed;
    private int skipped;

    public TagStats() { }

    public TagStats(final TagStatsList list, final String name, final int depth) {
        this.tagStatsList = list;
        this.name = name;
        this.depth = depth;
    }

    public void update(final Test test) {
        total++;
        if ("PASSED".equalsIgnoreCase(test.getResult())) {
            ++passed;
        } else if ("SKIPPED".equalsIgnoreCase(test.getResult())) {
            ++skipped;
        } else {
            ++failed;
        }
    }

}
