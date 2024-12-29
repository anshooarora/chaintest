package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SystemInfo {

    private long id;
    private String name;
    private String val;

    public SystemInfo() { }

    public SystemInfo(final String name, final String val) {
        this.name = name;
        this.val = val;
    }

}
