package com.aventstack.chainlp.api.tag;

import com.aventstack.chainlp.api.build.Build;
import com.aventstack.chainlp.api.test.Test;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString(exclude = {"builds", "tests"})
@EqualsAndHashCode(of = {"name"})
@NoArgsConstructor
@Entity
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Build> builds;

    @ManyToMany(mappedBy = "tags")
    @JsonIgnore
    private List<Test> tests;

    public Tag(final String name) {
        this.name = name;
    }

}
