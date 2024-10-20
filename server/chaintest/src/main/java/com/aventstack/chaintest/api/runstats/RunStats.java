package com.aventstack.chaintest.api.runstats;

import com.aventstack.chaintest.api.build.Build;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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

    @OneToOne(mappedBy = "runStats")
    @JsonIgnore
    private Build build;

    private int total;
    private int passed;
    private int failed;
    private int skipped;

}
