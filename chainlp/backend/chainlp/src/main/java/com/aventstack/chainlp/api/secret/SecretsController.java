package com.aventstack.chainlp.api.secret;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/6ff57e5")
public class SecretsController {

    @Autowired
    private SecretsService service;
    
    @PostMapping
    public void create(@Valid @RequestBody final Secret secret) {
        service.create(secret);
    }

    @PutMapping
    public void update(@Valid @RequestBody final Secret secret) {
        service.update(secret);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
