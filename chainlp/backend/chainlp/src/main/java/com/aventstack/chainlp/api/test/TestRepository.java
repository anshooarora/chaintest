package com.aventstack.chainlp.api.test;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface TestRepository extends
        PagingAndSortingRepository<Test, Long>, JpaSpecificationExecutor<Test>, CrudRepository<Test, Long> {

    List<TestStatView> findAllByBuildId(final Long buildId);

    void deleteByBuildId(final long buildId);

    Page<Test> findAllByBuildIdAndDepthAndTags_NameIn(final Long buildId, final Short depth,
                                                      final Set<String> tag, final Pageable pageable);

}