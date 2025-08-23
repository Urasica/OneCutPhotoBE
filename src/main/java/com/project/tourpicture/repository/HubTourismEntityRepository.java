package com.project.tourpicture.repository;

import com.project.tourpicture.dao.HubTourismEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HubTourismEntityRepository extends JpaRepository<HubTourismEntity, String> {
    List<HubTourismEntity> findByAreaCdAndSigunguCdAndBaseYm(String areaCd, String sigunguCd, String baseYm);
}
