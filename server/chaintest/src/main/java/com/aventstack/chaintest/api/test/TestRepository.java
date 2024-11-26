package com.aventstack.chaintest.api.test;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRepository extends
        PagingAndSortingRepository<Test, Long>, JpaSpecificationExecutor<Test> {

    void deleteByBuildId(final long buildId);

    Optional<Test> findByClientId(final String clientId);

}
