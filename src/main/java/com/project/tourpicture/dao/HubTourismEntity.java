package com.project.tourpicture.dao;

import com.project.tourpicture.dto.RegionBasedTouristDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class HubTourismEntity {
    @Id
    private String hubTatsCd;

    private String baseYm;
    private String areaCd;
    private String sigunguCd;

    private String mapX;
    private String mapY;

    private String hubTatsNm;
    private Integer hubRank;

    private String matchedContentId;

    @Transient
    private RegionBasedTouristDTO matchedTourist;
}
