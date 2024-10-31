package com.aventstack.chaintest.api.project;

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
@RequestMapping("/projects")
public class ProjectController {
    
    @Autowired
    private ProjectService service;

    @GetMapping
    public ResponseEntity<Page<Project>> findAll(final Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Project> find(@PathVariable final int id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public Project create(@Valid @RequestBody final Project project) {
        return service.create(project);
    }

    @PutMapping
    public ResponseEntity<Project> update(@Valid @RequestBody final Project project) {
        return ResponseEntity.ok(service.update(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
