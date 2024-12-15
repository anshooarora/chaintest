package com.aventstack.chainlp.api.test;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends
        PagingAndSortingRepository<Test, Long>, JpaSpecificationExecutor<Test>, CrudRepository<Test, Long> {

    List<TestStatView> findAllByBuildId(final Long buildId);

}