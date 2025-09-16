package com.project.tourpicture.repository;

import com.project.tourpicture.dao.CrowdBasedPhotoHigh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CrowdBasedPhotoHighRepository extends JpaRepository<CrowdBasedPhotoHigh, Long> {
    void deleteByAreaCdAndSigunguCd(String areaCd, String sigunguCd);

    @Modifying
    @Query("DELETE FROM CrowdBasedPhotoHigh c " +
            "WHERE CONCAT(c.areaCd, c.sigunguCd) NOT IN :codes")
    void deleteByAreaCdSigunguCdNotIn(@Param("codes") Set<String> codes);
}
