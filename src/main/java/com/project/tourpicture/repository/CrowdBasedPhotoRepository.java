package com.project.tourpicture.repository;

import com.project.tourpicture.dao.CrowdBasedPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface CrowdBasedPhotoRepository extends JpaRepository<CrowdBasedPhoto, Long> {
    List<CrowdBasedPhoto> findTop50ByPhotoIdNotIn(Set<Long> seenIds);
    List<CrowdBasedPhoto> findTop50By(); // 초기 요청 시
}

