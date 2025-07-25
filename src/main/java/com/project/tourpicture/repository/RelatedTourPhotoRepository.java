package com.project.tourpicture.repository;

import com.project.tourpicture.dao.RelatedTourPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RelatedTourPhotoRepository extends JpaRepository<RelatedTourPhoto, Long> {
    Optional<RelatedTourPhoto> findByOriginal(String original);

    Optional<RelatedTourPhoto> findFirstByOriginalContaining(String hubTatsNm);
}
