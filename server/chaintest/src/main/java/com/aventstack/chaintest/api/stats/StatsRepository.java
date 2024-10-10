package com.aventstack.chaintest.api.stats;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatsRepository extends CrudRepository<Stats, Long> {

    Optional<Stats> findByBuildId(final long buildId);

}
