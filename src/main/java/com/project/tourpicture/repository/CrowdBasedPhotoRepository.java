package com.project.tourpicture.repository;

import com.project.tourpicture.dao.CrowdBasedPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrowdBasedPhotoRepository extends JpaRepository<CrowdBasedPhoto, String> {
    @Query("SELECT c.contentId FROM CrowdBasedPhoto c")
    List<String> findAllContentIds();

    List<CrowdBasedPhoto> findByContentIdIn(List<String> contentIds);
}