package com.aventstack.chainlp.api.runstats;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RunStatsRepository extends CrudRepository<RunStats, Long> {

    Optional<RunStats> findByBuildId(final Long buildId);

    void deleteByBuildId(final Long builId);
}
