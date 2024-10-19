package com.aventstack.chaintest.api.tagstats;

import com.aventstack.chaintest.api.build.Build;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private Build build;

    private String name;
    private int total;
    private int passed;
    private int failed;
    private int skipped;

}
