package com.aventstack.chainlp.api.tagstats;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TagStatsService {

    @Autowired
    private TagStatsRepository repository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void updateAll(final List<TagStats> stats) {
        log.info("Updating list of TagStats: {}", stats);
        repository.saveAll(stats);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteForBuildId(final Long buildId) {
        log.info("Deleting all tag stats for build ID {}", buildId);
        repository.deleteByBuildId(buildId);
    }

}
