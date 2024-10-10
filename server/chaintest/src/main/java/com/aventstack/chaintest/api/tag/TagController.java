package com.aventstack.chaintest.api.tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/tags")
public class TagController {
    
    @Autowired
    private TagService service;

    @GetMapping
    public ResponseEntity<Page<Tag>> findAll(final Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tag> find(@PathVariable final long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<Tag> create(@Valid @RequestBody final Tag tag) {
        return ResponseEntity.ok(service.create(tag));
    }

    @PutMapping
    public ResponseEntity<Tag> update(@Valid @RequestBody final Tag tag) {
        return ResponseEntity.ok(service.update(tag));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
