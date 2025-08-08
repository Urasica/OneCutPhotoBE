package com.project.tourpicture.repository;

import com.project.tourpicture.dao.LclsSystemCd;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LclsSystemCdRepository extends JpaRepository<LclsSystemCd, String> {

    LclsSystemCd findByCode(String lclsSystemCd);
}
