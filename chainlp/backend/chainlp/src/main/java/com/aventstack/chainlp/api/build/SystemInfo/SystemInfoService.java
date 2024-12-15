package com.aventstack.chainlp.api.build.SystemInfo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Slf4j
@Service
public class SystemInfoService {

    @Autowired
    private SystemInfoRepository repository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveAll(final Collection<SystemInfo> systemInfo) {
        repository.saveAll(systemInfo);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteForBuildId(final Long buildId) {
        log.debug("Deleting SystemInfo for build ID {}", buildId);
        repository.deleteByBuildId(buildId);
        log.info("SystemInfo for build ID {} deleted", buildId);
    }

}
