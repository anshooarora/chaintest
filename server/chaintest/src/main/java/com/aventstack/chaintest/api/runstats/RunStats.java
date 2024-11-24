package com.aventstack.chaintest.api.runstats;

import com.aventstack.chaintest.api.build.Build;
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
@ToString(exclude = "build")
@Entity
@EqualsAndHashCode
@DiscriminatorOptions(force = true)
@Table(name = "run_stats")
public class RunStats {

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
    private int total;
    private int passed;
    private int failed;
    private int skipped;

    public RunStats() { }

    public RunStats(final Build build, final int depth) {
        this.build = build;
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
