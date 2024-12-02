package com.aventstack.chaintest.api.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Long>, JpaRepository<Tag, Long> {

    Page<Tag> findAll(final Pageable pageable);

    Optional<Tag> findByName(final String tag);

}
