package com.aventstack.chaintest.domain;

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

    @Override
    public void update(final Test test) {
        super.update(test);
    }

}
