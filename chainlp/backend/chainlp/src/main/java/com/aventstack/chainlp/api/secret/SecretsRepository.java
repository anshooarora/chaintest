package com.aventstack.chainlp.api.secret;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecretsRepository extends JpaRepository<Secret, Integer> {

    Optional<Secret> findByK(final String k);

}
