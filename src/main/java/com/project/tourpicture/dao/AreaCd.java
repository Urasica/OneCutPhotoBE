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
public class AreaCd {
    @Id
    @JsonProperty("code")
    private String areaCd;
    @JsonProperty("name")
    private String areaNm;
}
