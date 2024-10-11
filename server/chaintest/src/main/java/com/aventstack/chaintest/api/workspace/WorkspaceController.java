package com.aventstack.chaintest.api.workspace;

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
@RequestMapping("/workspaces")
public class WorkspaceController {
    
    @Autowired
    private WorkspaceService service;

    @GetMapping
    public ResponseEntity<Page<Workspace>> findAll(final Pageable pageable) {
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workspace> find(@PathVariable final int id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public Workspace create(@Valid @RequestBody final Workspace workspace) {
        return service.create(workspace);
    }

    @PutMapping
    public ResponseEntity<Workspace> update(@Valid @RequestBody final Workspace workspace) {
        return ResponseEntity.ok(service.update(workspace));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
