package com.project.tourpicture.dto.intro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "문화시설 카테고리 소개 정보")
public class CultureIntroDTO {

    @Schema(description = "애완동물동반가능정보")
    private String chkPet;

    @Schema(description = "문의및안내")
    private String infoCenter;

    @Schema(description = "주차시설")
    private String parking;

    @Schema(description = "주차요금")
    private String parkingFee;

    @Schema(description = "쉬는날")
    private String restDate;

    @Schema(description = "이용요금")
    private String useFee;

    @Schema(description = "이용시간")
    private String useTime;

    @Schema(description = "관람소요시간")
    private String spendTime;
}
