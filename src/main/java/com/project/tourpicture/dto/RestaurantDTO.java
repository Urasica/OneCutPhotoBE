package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "해당 지역의 식당 정보")
public class RestaurantDTO {

    @Schema(description = "식당명", example = "")
    private String name;

    @Schema(description = "식당 주소", example = "")
    private String address;

    @Schema(description = "X 좌표값", example = "")
    private String mapX;

    @Schema(description = "Y 좌표값", example = "")
    private String mapY;

    @Schema(description = "식당 이미지", example = "")
    private String imageUrl;
}
