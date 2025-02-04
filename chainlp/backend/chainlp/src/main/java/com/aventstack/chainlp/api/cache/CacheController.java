package com.aventstack.chainlp.api.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cache")
public class CacheController {

    @Autowired
    private CacheService service;

    @DeleteMapping
    public ResponseEntity<Void> clearAll() {
        service.clearAll();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> clear(@PathVariable final String name) {
        service.clearCache(name);
        return ResponseEntity.ok().build();
    }

}
