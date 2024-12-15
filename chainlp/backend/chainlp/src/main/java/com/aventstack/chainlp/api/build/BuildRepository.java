package com.aventstack.chainlp.api.build;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildRepository extends
        PagingAndSortingRepository<Build, Long>, JpaSpecificationExecutor<Build>, CrudRepository<Build, Long> {
}
