package com.aventstack.chaintest.api.runstats;

import com.aventstack.chaintest.api.build.Build;
import com.aventstack.chaintest.api.test.Test;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
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
@Table(name = "run_stat")
public class RunStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;

    @ManyToOne
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

    public RunStats(final Build build) {
        this.build = build;
    }

    public RunStats(final Build build, final int depth) {
        this.build = build;
        this.depth = depth;
    }

    public synchronized void update(final Test test) {
        ++total;
        if ("PASSED".equalsIgnoreCase(test.getResult())) {
            ++passed;
        } else if ("SKIPPED".equalsIgnoreCase(test.getResult())) {
            ++skipped;
        } else {
            ++failed;
        }
    }

}
