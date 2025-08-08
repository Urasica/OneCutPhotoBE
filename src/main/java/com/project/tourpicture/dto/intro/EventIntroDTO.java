package com.project.tourpicture.dto.intro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "행사/축제/공연 카테고리 소개 정보")
public class EventIntroDTO {

    @Schema(description = "관람가능연령")
    private String ageLimit;

    @Schema(description = "예매처")
    private String bookingPlace;

    @Schema(description = "행사홈페이지")
    private String eventHomepage;

    @Schema(description = "행사장소")
    private String eventPlace;

    @Schema(description = "공연시간")
    private String playTime;

    @Schema(description = "관람소요시간")
    private String spendTime;

    @Schema(description = "주최자연락처")
    private String sponsorTel;

    @Schema(description = "이용요금")
    private String useFee;
}


