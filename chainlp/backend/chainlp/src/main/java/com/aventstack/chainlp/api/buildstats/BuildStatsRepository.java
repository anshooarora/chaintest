package com.aventstack.chainlp.api.buildstats;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildStatsRepository extends CrudRepository<BuildStats, Long> {

    Optional<BuildStats> findByBuildId(final Long buildId);

    void deleteByBuildId(final Long builId);
}
