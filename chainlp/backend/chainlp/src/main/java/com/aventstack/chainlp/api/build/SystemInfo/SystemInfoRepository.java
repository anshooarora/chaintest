package com.aventstack.chainlp.api.build.SystemInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemInfoRepository extends JpaRepository<SystemInfo, Long> {

    void deleteByBuildId(final Long buildId);

}
