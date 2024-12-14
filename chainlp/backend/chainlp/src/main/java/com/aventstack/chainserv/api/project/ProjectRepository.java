package com.aventstack.chainserv.api.project;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface ProjectRepository extends
        PagingAndSortingRepository<Project, Integer>, JpaSpecificationExecutor<Project>, CrudRepository<Project, Integer> {

    Optional<Project> findByName(final String name);

}
