package com.aventstack.chainlp.api.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class CacheService {

    @Autowired
    private CacheManager cache;

    public void clearAll() {
        log.info("Clearing all caches");
        cache.getCacheNames().forEach(x -> Objects.requireNonNull(cache.getCache(x)).clear());
        log.info("All caches cleared");
    }

    public void clearCache(final String name) {
        log.info("Clearing cache: {}", name);
        Objects.requireNonNull(cache.getCache(name)).clear();
        log.info("Cache cleared: {}", name);
    }

}
