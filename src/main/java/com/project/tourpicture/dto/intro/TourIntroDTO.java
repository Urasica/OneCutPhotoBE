package com.project.tourpicture.dto.intro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "관광지 카테고리 소개 정보")
public class TourIntroDTO {

    @Schema(description = "애완동물동반가능정보")
    private String chkPet;

    @Schema(description = "체험가능연령")
    private String expAgeRange;

    @Schema(description = "문의및안내")
    private String infoCenter;

    @Schema(description = "개장일")
    private String openDate;

    @Schema(description = "주차시설")
    private String parking;

    @Schema(description = "쉬는날")
    private String restDate;

    @Schema(description = "이용시기")
    private String useSeason;

    @Schema(description = "이용시간")
    private String useTime;
}
