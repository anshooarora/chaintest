package com.aventstack.chainlp.api.buildstats;

import com.aventstack.chainlp.api.build.Build;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = "build")
@Entity
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buildstats")
public class BuildStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @EqualsAndHashCode.Exclude
    private long id;

    @ManyToOne
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Build build;

    private Integer depth = 0;
    private int total;
    private int passed;
    private int failed;
    private int skipped;

    public void update(final String result) {
        ++total;
        if ("PASSED".equalsIgnoreCase(result)) {
            ++passed;
        } else if ("SKIPPED".equalsIgnoreCase(result)) {
            ++skipped;
        } else {
            ++failed;
        }
    }

}
