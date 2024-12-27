package com.aventstack.chainlp.api.secret;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecretsRepository extends JpaRepository<Secret, Integer> {
}
