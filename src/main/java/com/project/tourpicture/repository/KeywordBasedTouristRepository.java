package com.project.tourpicture.repository;

import com.project.tourpicture.dao.KeywordBasedTourist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordBasedTouristRepository extends JpaRepository<KeywordBasedTourist, String> {
    List<KeywordBasedTourist> findByKeyword(String keyWord);
    void deleteByKeyword(String keyWord);
    Optional<KeywordBasedTourist> findByContentId(String contentId);
}
