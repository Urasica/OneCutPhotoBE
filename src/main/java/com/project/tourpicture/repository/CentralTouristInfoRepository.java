package com.project.tourpicture.repository;

import com.project.tourpicture.dao.CentralTouristInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CentralTouristInfoRepository extends JpaRepository<CentralTouristInfo, String> {
    List<CentralTouristInfo> findByAreaCdAndSignguCdOrderByHubRankAsc(String areaCd, String signguCd);
    Optional<CentralTouristInfo> findByHubTatsNm(String hubTatsNm);
}
