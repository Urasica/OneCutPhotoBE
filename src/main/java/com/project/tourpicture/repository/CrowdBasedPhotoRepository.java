package com.project.tourpicture.repository;

import com.project.tourpicture.dao.CrowdBasedPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CrowdBasedPhotoRepository extends JpaRepository<CrowdBasedPhoto, Long> {
    @Query("SELECT p.photoId FROM CrowdBasedPhoto p")
    List<Long> findAllIds();

    List<CrowdBasedPhoto> findByPhotoIdIn(List<Long> ids);

}