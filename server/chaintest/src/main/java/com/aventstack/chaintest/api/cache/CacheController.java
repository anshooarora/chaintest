package com.aventstack.chaintest.api.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class CacheController {

    @Autowired
    private CacheService service;

    @DeleteMapping
    public ResponseEntity<Void> delete() {
        service.deleteAll();
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> delete(@PathVariable final String name) {
        service.delete(name);
        return ResponseEntity.ok().build();
    }

}
