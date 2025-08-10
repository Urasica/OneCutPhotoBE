package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "연관관광지 정보")
public class RelatedTourDTO {

    @Schema(description = "콘텐츠 ID")
    private String contentId;

    @Schema(description = "관광타입 ID")
    private String contentTypeId;

    @Schema(description = "관광지명")
    private String title;

    @Schema(description = "이미지")
    private String imageUrl;

    @Schema(description = "주소")
    private String address;
}