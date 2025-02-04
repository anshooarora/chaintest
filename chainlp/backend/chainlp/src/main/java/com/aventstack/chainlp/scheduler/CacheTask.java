package com.aventstack.chainlp.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CacheTask {

    @Autowired
    private CacheManager cacheManager;

    @Scheduled(cron="0 0 0 * * ?")
    public void reportCurrentTime() {
        cacheManager.getCacheNames().forEach(cacheManager::getCache);
    }

}
