package com.aventstack.chaintest.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tag {

    private String name;

    public Tag() { }

    public Tag(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        for (int i = 0; i < name.length(); i++) {
            hash = hash*31 + name.charAt(i);
        }
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        return null != name && name.equalsIgnoreCase(((Tag) obj).getName());
    }

}
