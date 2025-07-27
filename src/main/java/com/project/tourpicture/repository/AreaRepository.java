package com.project.tourpicture.repository;

import com.project.tourpicture.dao.AreaEntity;
import com.project.tourpicture.dao.AreaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AreaRepository extends JpaRepository<AreaEntity, AreaId> {
    void deleteByIdAreaCd(String areaCd);

    List<AreaEntity> findByIdAreaCd(String areaCd);

    List<AreaEntity> findByIdSigunguCd(String sigunguCd);
}

