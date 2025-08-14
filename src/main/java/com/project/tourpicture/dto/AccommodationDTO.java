package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "해당 지역의 숙소 정보")
public class AccommodationDTO {

    @Schema(description = "숙소명", example = "설악포유리조트")
    private String name;

    @Schema(description = "숙소 주소", example = "강원특별자치도 고성군 토성면 잼버리동로 97")
    private String address;

    @Schema(description = "X 좌표값", example = "128.5195664383")
    private String mapX;

    @Schema(description = "Y 좌표값", example = "38.2337859469")
    private String mapY;

    @Schema(description = "숙소 이미지", example = "http://tong.visitkorea.or.kr/cms/resource/52/1581452_image2_1.jpg")
    private String imageUrl;
}
