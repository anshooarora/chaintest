package com.aventstack.chainlp.api.appenv;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

@RestController
@RequestMapping("/appenv")
public class AppEnvController {

    @Autowired
    private AppEnvService service;

    @GetMapping
    public ResponseEntity<List<AppEnv>> findAll(@RequestParam(required = false, defaultValue = "0") final int id,
                                                @RequestParam(required = false) final String name) {
        return ResponseEntity.ok(service.findAll(id, name));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppEnv> find(@PathVariable final int id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public AppEnv create(@Valid @RequestBody final AppEnv appEnv) {
        return service.create(appEnv);
    }

    @PutMapping
    public ResponseEntity<Void> update(@Valid @RequestBody final AppEnv appEnv) {
        service.update(appEnv);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final int id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }

}
