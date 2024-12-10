package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunStats extends Stat {

    public RunStats(final int depth) {
        setDepth((depth));
    }

    public void update(final Test test) {
        super.update(test);
    }

}
