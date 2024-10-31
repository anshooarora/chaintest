package com.aventstack.chaintest.api.project;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends PagingAndSortingRepository<Project, Integer> {

    Optional<Project> findByName(final String name);

}
