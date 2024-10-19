package com.aventstack.chaintest.api.tagstats;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagStatsRepository extends CrudRepository<TagStats, Long> {

    Optional<TagStats> findByBuildId(final long buildId);

}
