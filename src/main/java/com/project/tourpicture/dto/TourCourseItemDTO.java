package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "추천 코스 관광지 정보")
public class TourCourseItemDTO {

    @Schema(description = "관광지명", example = "광장시장")
    private String tourName;

    @Schema(description = "X 좌표값", example = "126.889241804891...")
    private String mapX;

    @Schema(description = "Y 좌표값", example = "37.508691159691...")
    private String mapY;
}
