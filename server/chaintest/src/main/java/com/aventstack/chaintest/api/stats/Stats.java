package com.aventstack.chaintest.api.stats;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@ToString
@Entity
@Table(name = "stats")
public class Stats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "build")
    private long buildId;

    @Column(name = "tests_count")
    private int testsCount;

    @Column(name = "tests_passed_count")
    private int testsPassedCount;

    @Column(name = "tests_failed_count")
    private int testsFailedCount;

    @Column(name = "tests_skipped_count")
    private int testsSkippedCount;

}
