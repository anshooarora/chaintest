package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TagStats {

    private String name;

    public TagStats() { }

    public TagStats(final int depth) {
        setDepth((depth));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private int depth;
    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private long durationMs;

    public void update(final Test test) {
        total++;
        if (Result.PASSED.getResult().equalsIgnoreCase(test.getResult())) {
            ++passed;
        } else if (Result.SKIPPED.getResult().equalsIgnoreCase(test.getResult())) {
            ++skipped;
        } else {
            ++failed;
        }
        durationMs += test.getDurationMs();
    }

    public String getDurationPretty() {
        return TimeUtil.getPrettyTime(getDurationMs());
    }
}
