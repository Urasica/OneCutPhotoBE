package com.project.tourpicture.repository;

import com.project.tourpicture.dao.RegionBasedTourist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionBasedTouristRepository extends JpaRepository<RegionBasedTourist, String> {
    List<RegionBasedTourist> findByAreaCdAndSigunguCd(String areaCd, String sigunguCd);
    Optional<RegionBasedTourist> findByTitle(String title);
    void deleteByAreaCdAndSigunguCd(String areaCd, String sigunguCd);
}
