package com.aventstack.chaintest.api.build.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
public class SystemInfoService {

    @Autowired
    private SystemInfoRepository repository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveAll(final Collection<SystemInfo> systemInfo) {
        repository.saveAll(systemInfo);
    }

}
