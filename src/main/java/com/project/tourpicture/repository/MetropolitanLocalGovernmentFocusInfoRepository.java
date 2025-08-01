package com.project.tourpicture.repository;

import com.project.tourpicture.dao.MetropolitanLocalGovernmentFocusInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MetropolitanLocalGovernmentFocusInfoRepository extends JpaRepository<MetropolitanLocalGovernmentFocusInfo, Long> {

    @Query("SELECT DISTINCT m.areaCode, m.areaNm FROM MetropolitanLocalGovernmentFocusInfo m")
    List<Object[]> findDistinctAreaCodeAndName();
}
