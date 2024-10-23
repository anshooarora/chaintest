package com.aventstack.chaintest.api.domain;

import com.aventstack.chaintest.api.runstats.RunStats;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "stats")
public class Stat {

    @Id
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "run_stats_id")
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private RunStats runStats;

    private int depth;
    private int total;
    private int passed;
    private int failed;
    private int skipped;

}
