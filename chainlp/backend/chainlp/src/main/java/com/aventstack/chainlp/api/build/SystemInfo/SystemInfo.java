package com.aventstack.chainlp.api.build.SystemInfo;

import com.aventstack.chainlp.api.build.Build;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Data
@ToString(exclude = "build")
@Entity
@NoArgsConstructor
@Table(name = "systeminfo")
public class SystemInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private long id;

    @ManyToOne
    @JoinColumn
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Build build;

    private String name;
    private String value;

}
