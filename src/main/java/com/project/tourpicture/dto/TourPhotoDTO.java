package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "관광지 대표이미지 정보")
public class TourPhotoDTO {

    @Schema(description = "이미지 링크", example = "http://tong.visitkorea.or.kr/cms2/website/62/1825662.jpg")
    String imageUrl;

    @Schema(description = "촬영 연월", example = "201306")
    String takenMonth;
}
