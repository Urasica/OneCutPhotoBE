package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MetropolitanLocalGovernmentFocusInfoDTO {
    @Schema(description = "기준 일자", example = "20250712")
    private String baseYmd;

    @Schema(description = "시도 코드", example = "11")
    private String areaCd;

    @Schema(description = "관광객 구분 코드", example = "(1:현지인(a),2:외지인(b),3:외국인(c))")
    private String touDivCd;

    @Schema(description = "시도 명", example = "서울특별시")
    private String areaNm;

    @Schema(description = "관광객 구분명", example = "현지인")
    private String touDivNm;

    @Schema(description = "요일 구분 코드", example = "(1:월요일,2:화요일,3:수요일,4:목요일,5:금요일,6:토요일,7:일요일)")
    private String daywkDivCd;

    @Schema(description = "요일 구분명", example = "목요일")
    private String daywkDivNm;

    @Schema(description = "관광객 수", example = "176473.5")
    private String touNum;
}
