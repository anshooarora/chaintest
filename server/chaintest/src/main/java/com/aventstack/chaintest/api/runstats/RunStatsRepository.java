package com.aventstack.chaintest.api.runstats;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RunStatsRepository extends CrudRepository<RunStats, Long> {

    Optional<RunStats> findByBuildId(final long buildId);

}
