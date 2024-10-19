package com.aventstack.chaintest.api.tagstats;

import com.aventstack.chaintest.api.build.Build;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@ToString(exclude = "build")
@Entity
@EqualsAndHashCode
@Table(name = "tag_stats")
public class TagStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "build_id", insertable = false, updatable = false)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Build build;

    @Column(name = "build_id")
    private long buildId;

    private String name;
    private int total;
    private int passed;
    private int failed;
    private int skipped;

}
