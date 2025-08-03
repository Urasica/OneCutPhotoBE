package com.project.tourpicture.repository;

import com.project.tourpicture.dao.RegionBasedTourist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegionBasedTouristRepository extends JpaRepository<RegionBasedTourist, String> {
    List<RegionBasedTourist> findByAreaCdAndSigunguCd(String areaCd, String sigunguCd);

    void deleteByAreaCdAndSigunguCd(String areaCd, String sigunguCd);
}
