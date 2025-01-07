package com.aventstack.chainlp.api.secret;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "a7f9d86")
public class Secret {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String k;
    private String v;

    private transient String decoded;

}
