package com.project.tourpicture.dao;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AreaEntity {
    @EmbeddedId
    private AreaId id;

    private String areaNm;

    private String sigunguNm;

    public String getAreaCd() {
        return id.getAreaCd();
    }

    public String getSigunguCd() {
        return id.getSigunguCd();
    }
}
