package com.aventstack.chaintest.domain;

public class TagStats extends RunStats {

    private String name;

    public TagStats() {
        super();
    }

    public TagStats(final long buildId) {
        super(buildId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
