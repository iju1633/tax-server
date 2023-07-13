package com.tax.o3server.repository;

import com.tax.o3server.entity.ScrapData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapDataRepository extends JpaRepository<ScrapData, Long> { // 스크랩 데이터에 활용
}
