package com.aventstack.chainlp.api.appenv;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppEnvRepository extends
        JpaRepository<AppEnv, Integer>, JpaSpecificationExecutor<AppEnv> {

    Optional<AppEnv> findByK(final String name);

}
