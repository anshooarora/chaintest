package com.aventstack.chaintest.domain;

public class Stats {

    private int testsCount;
    private int testsPassedCount;
    private int testsFailedCount;
    private int testsSkippedCount;

    public int getTestsCount() {
        return testsCount;
    }

    public void setTestsCount(int testsCount) {
        this.testsCount = testsCount;
    }

    public int getTestsPassedCount() {
        return testsPassedCount;
    }

    public void setTestsPassedCount(int testsPassedCount) {
        this.testsPassedCount = testsPassedCount;
    }

    public int getTestsFailedCount() {
        return testsFailedCount;
    }

    public void setTestsFailedCount(int testsFailedCount) {
        this.testsFailedCount = testsFailedCount;
    }

    public int getTestsSkippedCount() {
        return testsSkippedCount;
    }

    public void setTestsSkippedCount(int testsSkippedCount) {
        this.testsSkippedCount = testsSkippedCount;
    }

}
