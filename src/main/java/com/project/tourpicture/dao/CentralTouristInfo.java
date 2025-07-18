package com.project.tourpicture.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "중심관광지 정보")
public class CentralTouristInfo {
    @JsonProperty("baseYm")
    @Schema(description = "기준 년월", example = "202507")
    private String baseYm;

    @JsonProperty("mapX")
    @Schema(description = "X 좌표값", example = "126.889241804891...")
    private String mapX;

    @JsonProperty("mapY")
    @Schema(description = "Y 좌표값", example = "37.508691159691...")
    private String mapY;

    @JsonProperty("areaCd")
    @Schema(description = "지역 코드", example = "11")
    private String areaCd;

    @JsonProperty("areaNm")
    @Schema(description = "지역 명", example = "서울특별시")
    private String areaNm;

    @JsonProperty("signguCd")
    @Schema(description = "시군구 코드", example = "11530")
    private String signguCd;

    @JsonProperty("signguNm")
    @Schema(description = "시군구 명", example = "구로구")
    private String signguNm;

    @Id
    @JsonProperty("hubTatsCd")
    @Schema(description = "중심 관광지 코드", example = "b5ef6787d594080cd54b65a9bc884a9b")
    private String hubTatsCd;

    @JsonProperty("hubTatsNm")
    @Schema(description = "중심 관광지 명", example = "현대백화점/디큐브시티점")
    private String hubTatsNm;

    @JsonProperty("hubCtgryLclsNm")
    @Schema(description = "카테고리 대분류 명", example = "관광지")
    private String hubCtgryLclsNm;

    @JsonProperty("hubCtgryMclsNm")
    @Schema(description = "카테고리 중분류 명", example = "쇼핑")
    private String hubCtgryMclsNm;

    @JsonProperty("hubRank")
    @Schema(description = "중심지 순위", example = "1")
    private int hubRank;
}
