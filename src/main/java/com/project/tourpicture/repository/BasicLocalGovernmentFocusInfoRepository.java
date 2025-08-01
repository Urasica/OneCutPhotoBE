package com.project.tourpicture.repository;

import com.project.tourpicture.dao.BasicLocalGovernmentFocusInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasicLocalGovernmentFocusInfoRepository extends JpaRepository<BasicLocalGovernmentFocusInfo, Long> {
    List<BasicLocalGovernmentFocusInfo> findBySignguCdStartingWith(String areaCd);

    @Query("SELECT DISTINCT b.signguCd, b.signguNm FROM BasicLocalGovernmentFocusInfo b WHERE b.signguCd LIKE CONCAT(:areaCode, '%')")
    List<Object[]> findDistinctSignguCodeAndNameByAreaCodePrefix(@Param("areaCode") String areaCode);
}