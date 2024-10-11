package com.aventstack.chaintest.api.workspace;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends PagingAndSortingRepository<Workspace, Integer> {
}
