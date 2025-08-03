package com.project.tourpicture.repository;

import com.project.tourpicture.dao.MetropolitanLocalGovernmentFocusInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface MetropolitanLocalGovernmentFocusInfoRepository extends JpaRepository<MetropolitanLocalGovernmentFocusInfo, Long> {

}
