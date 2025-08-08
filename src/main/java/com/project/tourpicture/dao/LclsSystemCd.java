package com.project.tourpicture.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Schema(description = "분류체계 코드")
public class LclsSystemCd {
    @Id
    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;
}
