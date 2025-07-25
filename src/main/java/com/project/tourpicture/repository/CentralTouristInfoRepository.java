package com.project.tourpicture.repository;

import com.project.tourpicture.dao.CentralTouristInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CentralTouristInfoRepository extends JpaRepository<CentralTouristInfo, String> {
    List<CentralTouristInfo> findByAreaCdAndSignguCdOrderByHubRankAsc(String areaCd, String signguCd);

    List<CentralTouristInfo> findTop5BySignguCdOrderByHubRankAsc(String signguCd);
}
