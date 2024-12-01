package com.aventstack.chaintest.api.test;

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

import javax.validation.Valid;
import java.util.Set;

@RestController
@RequestMapping("/tests")
public class TestController {
    
    @Autowired
    private TestService service;

    @GetMapping
    public ResponseEntity<Page<Test>> findAll(@RequestParam(required = false) final String name,
                                              @RequestParam(required = false, defaultValue = "0") final Integer projectId,
                                              @RequestParam(required = false, defaultValue = "0") final Long buildId,
                                              @RequestParam(required = false) final Integer depth,
                                              @RequestParam(required = false) final String result,
                                              @RequestParam(required = false) final Set<String> tags,
                                              @RequestParam(required = false) final String error,
                                              @RequestParam(required = false) final String op,
                                              final Pageable pageable) {
        return ResponseEntity.ok(service.findAll(name, projectId, buildId, depth, result, tags, error, op, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Test> find(@PathVariable final long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public Test create(@Valid @RequestBody final Test test) {
        return service.create(test);
    }

    @PutMapping
    public ResponseEntity<Test> update(@Valid @RequestBody final Test test) {
        return ResponseEntity.ok(service.update(test));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
    
}
