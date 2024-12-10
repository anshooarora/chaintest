package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Stat {

    private int depth;
    private int total;
    private int passed;
    private int failed;
    private int skipped;

    public Stat(final int depth) {
        this.depth = depth;
    }

    public void update(final Test test) {
        total++;
        if (Result.PASSED.getResult().equalsIgnoreCase(test.getResult())) {
            ++passed;
        } else if (Result.SKIPPED.getResult().equalsIgnoreCase(test.getResult())) {
            ++skipped;
        } else {
            ++failed;
        }
    }

}
