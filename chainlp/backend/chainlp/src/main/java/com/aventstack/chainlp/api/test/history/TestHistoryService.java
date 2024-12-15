package com.aventstack.chainlp.api.test.history;

import com.aventstack.chainlp.api.test.Test;
import com.aventstack.chainlp.api.test.TestRepository;
import com.aventstack.chainlp.api.test.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestHistoryService {

    @Autowired
    private TestRepository repository;

    @Autowired
    private TestService service;

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Page<Test> forId(final long id, final Pageable pageable) {
        final Test test = service.findById(id);
        return forTest(test, pageable);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Page<Test> forTest(final Test test, final Pageable pageable) {
        final TestHistorySpec spec = new TestHistorySpec(test);
        return repository.findAll(spec, pageable);
    }

}
