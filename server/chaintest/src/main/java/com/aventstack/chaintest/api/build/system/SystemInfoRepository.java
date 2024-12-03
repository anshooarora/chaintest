package com.aventstack.chaintest.api.build.system;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemInfoRepository extends JpaRepository<SystemInfo, Long> {
}
