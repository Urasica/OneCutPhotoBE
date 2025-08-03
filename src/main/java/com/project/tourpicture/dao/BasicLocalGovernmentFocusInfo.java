package com.project.tourpicture.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Schema(description = "기초 지자체 집중도 정보")
@IdClass(BasicLocalGovernmentFocusInfoId.class)
public class BasicLocalGovernmentFocusInfo {
    @Id
    @JsonProperty("baseYmd")
    @Schema(description = "기준 일자", example = "20250712")
    private String baseYmd;

    @Id
    @JsonProperty("signguCode")
    @Schema(description = "시군구 코드", example = "11110")
    private String sigunguCd;

    @Id
    @JsonProperty("touDivCd")
    @Schema(description = "관광객 구분 코드", example = "(1:현지인(a),2:외지인(b),3:외국인(c))")
    private String touDivCd;

    @JsonProperty("signguNm")
    @Schema(description = "시군구 명", example = "종로구")
    private String sigunguNm;

    @JsonProperty("touDivNm")
    @Schema(description = "관광객 구분명", example = "현지인")
    private String touDivNm;

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
