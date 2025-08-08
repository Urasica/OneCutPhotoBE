package com.project.tourpicture.dto.intro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "여행코스 카테고리 소개 정보")
public class TourCourseIntroDTO {

    @Schema(description = "코스총거리")
    private String distance;

    @Schema(description = "문의및안내")
    private String infoCenter;

    @Schema(description = "코스일정")
    private String schedule;

    @Schema(description = "코스총소요시간")
    private String takeTime;

    @Schema(description = "코스테마")
    private String theme;
}
