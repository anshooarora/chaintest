package com.aventstack.chaintest.api.runstats;

import com.aventstack.chaintest.api.build.Build;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
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
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "build_id", insertable = false, updatable = false)
    @JsonBackReference
    private Build build;

    @Column(name = "build_id")
    private long buildId;

    private int total;
    private int passed;
    private int failed;
    private int skipped;

}
