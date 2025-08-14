package com.project.tourpicture.repository;

import com.project.tourpicture.dao.RegionBasedTourist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegionBasedTouristRepository extends JpaRepository<RegionBasedTourist, String> {
    List<RegionBasedTourist> findByAreaCdAndSigunguCd(String areaCd, String sigunguCd);
    void deleteByAreaCdAndSigunguCd(String areaCd, String sigunguCd);
    List<RegionBasedTourist> findByAreaCdAndSigunguCdAndContentTypeId(String areaCd, String sigunguCd, String contentTypeId);
    void deleteByAreaCdAndSigunguCdAndContentTypeId(String areaCd, String sigunguCd, String contentTypeId);
    Optional<RegionBasedTourist> findByContentId(String contentId);
}
