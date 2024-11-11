package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public int getSkipped() {
        return skipped;
    }

    public void setSkipped(int skipped) {
        this.skipped = skipped;
    }

}
