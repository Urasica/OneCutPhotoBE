package com.project.tourpicture.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SigunguCd {
    @JsonProperty("code")
    private String sigunguCd;

    @JsonProperty("name")
    private String sigunguNm;

    @Id
    private String totalCd;
}
