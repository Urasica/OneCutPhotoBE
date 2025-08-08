package com.project.tourpicture.dto.intro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "레포츠 카테고리 소개 정보")
public class LeportsIntroDTO {

    @Schema(description = "애완동물동반가능정보")
    private String chkPet;

    @Schema(description = "체험가능연령")
    private String expAgeRange;

    @Schema(description = "문의및안내")
    private String infoCenter;

    @Schema(description = "주차시설")
    private String parking;

    @Schema(description = "주차요금")
    private String parkingFee;

    @Schema(description = "예약안내")
    private String reservation;

    @Schema(description = "쉬는날")
    private String restDate;

    @Schema(description = "입장료")
    private String useFee;
}
