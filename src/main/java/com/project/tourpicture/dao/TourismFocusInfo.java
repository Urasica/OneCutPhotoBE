package com.project.tourpicture.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "관광지 집중도 정보")
public class TourismFocusInfo {
    @JsonProperty("baseYmd")
    @Schema(description = "기준 일자", example = "20250715")
    private String baseYmd;

    @JsonProperty("areaCd")
    @Schema(description = "지역 코드", example = "11")
    private String areaCd;

    @JsonProperty("areaNm")
    @Schema(description = "지역명", example = "서울특별시")
    private String areaNm;

    @JsonProperty("signguCd")
    @Schema(description = "시군구 코드", example = "11110")
    private String signguCd;

    @JsonProperty("signguNm")
    @Schema(description = "시군구 명", example = "종로구")
    private String signguNm;

    @JsonProperty("tAtsNm")
    @Schema(description = "관광지 명", example = "경복궁")
    private String tAtsNm;

    @JsonProperty("cnctrRate")
    @Schema(description = "집중률", example = "100")
    private double cnctrRate;
}

