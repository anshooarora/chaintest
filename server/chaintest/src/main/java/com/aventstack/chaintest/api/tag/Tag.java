package com.aventstack.chaintest.api.tag;

import com.aventstack.chaintest.api.build.Build;
import com.aventstack.chaintest.api.test.Test;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@ToString(exclude = {"builds", "tests"})
@EqualsAndHashCode(of = "name")
@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;

    @Column
    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Build> builds;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Test> tests;

    public Tag() { }

    public Tag(final String name) {
        this.name = name;
    }

}
