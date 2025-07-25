package com.project.tourpicture.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class GovernmentVisitInfo {
    @JsonProperty("baseYmd")
    @Schema(description = "기준 일자", example = "20250712")
    private String baseYmd;

    @Id
    @JsonProperty("signguCode")
    @Schema(description = "시군구 코드", example = "11110")
    private String signguCd;

    @JsonProperty("signguNm")
    @Schema(description = "시군구 명", example = "종로구")
    private String signguNm;

    @JsonProperty("daywkDivCd")
    @Schema(description = "요일 구분 코드", example = "(1:월요일,2:화요일,3:수요일,4:목요일,5:금요일,6:토요일,7:일요일)")
    private String daywkDivCd;

    @JsonProperty("daywkDivNm")
    @Schema(description = "요일 구분명", example = "목요일")
    private String daywkDivNm;

    @JsonProperty("touNum")
    @Schema(description = "관광객 수", example = "176473.5")
    private Double touNum;
}