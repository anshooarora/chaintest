package com.aventstack.chaintest.api.test;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends
        PagingAndSortingRepository<Test, Long>, JpaSpecificationExecutor<Test> {
}
