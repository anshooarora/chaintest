package com.aventstack.chaintest.api.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class CacheService {

    @Autowired
    private CacheManager cacheManager;

    public void deleteAll() {
        cacheManager.getCacheNames()
                .forEach(name -> Objects.requireNonNull(cacheManager.getCache(name)).clear());
    }

    public void delete(final String name) {
        Optional.ofNullable(cacheManager.getCache(name)).ifPresent(Cache::clear);
    }

}
