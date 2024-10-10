package com.aventstack.chaintest.api.test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends PagingAndSortingRepository<Test, Long>  {

    Page<Test> findAll(final Pageable pageable);

}
