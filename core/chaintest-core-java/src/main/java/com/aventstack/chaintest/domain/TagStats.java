package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagStats extends Stat {

    private String name;

    public TagStats(final int depth) {
        super(depth);
    }

    private long durationMs;

    @Override
    public void update(final Test test) {
        super.update(test);
        durationMs += test.getDurationMs();
    }

    public String getDurationPretty() {
        return TimeUtil.getPrettyTime(getDurationMs());
    }
}
