package com.aventstack.chainlp.api.build;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/builds")
public class BuildController {

    @Autowired
    private BuildService service;

    @GetMapping
    public ResponseEntity<Page<Build>> findAll(@RequestParam(required = false, defaultValue = "0") final long id,
                                               @RequestParam(required = false) final Integer projectId,
                                               final Pageable pageable) {
        return ResponseEntity.ok(service.findAll(id, projectId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Build> find(@PathVariable final long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public Build create(@Valid @RequestBody final Build build) {
        return service.create(build);
    }

    @PutMapping
    public ResponseEntity<Build> update(@Valid @RequestBody final Build build) {
        return ResponseEntity.ok(service.update(build));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
