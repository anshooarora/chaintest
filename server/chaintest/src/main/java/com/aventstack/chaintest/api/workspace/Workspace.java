package com.aventstack.chaintest.api.workspace;

import com.aventstack.chaintest.api.build.Build;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Data
@ToString(exclude = "builds")
@Entity
@Table(name = "workspace")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private long createdAt;

    @Column
    private String name;

    @OneToMany(mappedBy = "workspace")
    @JsonIgnore
    private List<Build> builds;

}
