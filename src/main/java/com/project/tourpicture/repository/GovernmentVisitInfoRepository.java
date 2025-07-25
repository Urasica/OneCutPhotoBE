package com.project.tourpicture.repository;

import com.project.tourpicture.dao.GovernmentVisitInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GovernmentVisitInfoRepository extends JpaRepository<GovernmentVisitInfo, String> {
    List<GovernmentVisitInfo> findTop30ByBaseYmdOrderByTouNumAsc(String baseYmd);

    List<GovernmentVisitInfo> findTop30ByOrderByTouNumAsc();
}
