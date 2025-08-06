package com.project.tourpicture.repository;

import com.project.tourpicture.dao.CrowdBasedPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CrowdBasedPhotoRepository extends JpaRepository<CrowdBasedPhoto, String> {
}