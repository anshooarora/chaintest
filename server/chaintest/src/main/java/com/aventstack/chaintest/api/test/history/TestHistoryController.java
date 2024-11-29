package com.aventstack.chaintest.api.test.history;

import com.aventstack.chaintest.api.test.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tests")
public class TestHistoryController {

    @Autowired
    private TestHistoryService service;

    @GetMapping("/{id}/history")
    public ResponseEntity<Page<Test>> find(@PathVariable final long id, final Pageable pageable) {
        return ResponseEntity.ok(service.forId(id, pageable));
    }

}
