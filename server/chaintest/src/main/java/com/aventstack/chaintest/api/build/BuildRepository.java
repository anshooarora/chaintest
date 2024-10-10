package com.aventstack.chaintest.api.build;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildRepository extends PagingAndSortingRepository<Build, Long>  {

    Page<Build> findAll(final Pageable pageable);

}
