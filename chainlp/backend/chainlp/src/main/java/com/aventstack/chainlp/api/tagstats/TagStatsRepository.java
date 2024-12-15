package com.aventstack.chainlp.api.tagstats;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagStatsRepository extends CrudRepository<TagStats, Long> {

    void deleteByBuildId(final Long builId);

}

