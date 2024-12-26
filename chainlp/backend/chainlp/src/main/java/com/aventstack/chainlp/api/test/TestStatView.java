package com.aventstack.chainlp.api.test;

import com.aventstack.chainlp.api.tag.Tag;

import java.util.Set;

public interface TestStatView {

    Integer getDepth();
    String getResult();
    Set<Tag> getTags();
    Long getDurationMs();

}
