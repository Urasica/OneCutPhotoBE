package com.project.tourpicture.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HubTourism {
    @JsonProperty("baseYm")
    @Schema(description = "기준 연월", example = "202507")
    private String baseYm;

    @JsonProperty("mapX")
    @Schema(description = "X 좌표", example = "126.88924180...")
    private String mapX;

    @JsonProperty("mapY")
    @Schema(description = "Y 좌표", example = "37.50869115...")
    private String mapY;

    @JsonProperty("areaCd")
    @Schema(description = "시도 코드", example = "11")
    private String areaCd;

    @JsonProperty("areaNm")
    @Schema(description = "시도명", example = "서울특별시")
    private String areaNm;

    @JsonProperty("signguCd")
    @Schema(description = "시군구 코드", example = "11530")
    private String sigunguCd;

    @JsonProperty("signguNm")
    @Schema(description = "시군구 명", example = "구로구")
    private String sigunguNm;

    @JsonProperty("hubTatsCd")
    @Schema(description = "중심관광지코드", example = "b5ef6787d594080cd54b65a9bc884a9b")
    private String hubTatsCd;

    @JsonProperty("hubTatsNm")
    @Schema(description = "중심관광지명", example = "현대백화점")
    private String hubTatsNm;

    @JsonProperty("hubRank")
    @Schema(description = "순위", example = "1")
    private String hubRank;
}
