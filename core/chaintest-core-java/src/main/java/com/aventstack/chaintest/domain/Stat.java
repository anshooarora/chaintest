package com.aventstack.chaintest.domain;

import com.aventstack.chaintest.util.TimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Stat {

    protected Set<String> clientId = ConcurrentHashMap.newKeySet();

    private int depth;
    private int total;
    private int passed;
    private int failed;
    private int skipped;
    private long durationMs;

    public Stat(final int depth) {
        this.depth = depth;
    }

    protected void update(final Test test) {
        if (clientId.contains(test.getClientId().toString())) {
            return;
        }
        clientId.add(test.getClientId().toString());
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
