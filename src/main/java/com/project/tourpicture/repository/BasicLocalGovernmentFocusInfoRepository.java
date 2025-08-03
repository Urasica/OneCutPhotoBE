package com.project.tourpicture.repository;

import com.project.tourpicture.dao.BasicLocalGovernmentFocusInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicLocalGovernmentFocusInfoRepository extends JpaRepository<BasicLocalGovernmentFocusInfo, Long> {
    List<BasicLocalGovernmentFocusInfo> findBySigunguCdStartingWith(String areaCd);

}