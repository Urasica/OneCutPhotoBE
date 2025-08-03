package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "연관관광지 정보")
public class RelatedTourDTO {

    @Schema(description = "관광지명", example = "광장시장")
    private String relatedTourName;

    @Schema(description = "관광지 이미지", example = "http://tong.visitkorea.or.kr/cms/resource/81/2668981_image2_1.jpg")
    private String imageUrl;

    @Schema(description = "시도코드", example = "11")
    private String relatedTourAreaCode;

    @Schema(description = "지역명", example = "서울특별시")
    private String relatedTourAreaName;

    @Schema(description = "시군구코드", example = "110")
    private String relatedTourSigunguCode;

    @Schema(description = "시군구명", example = "종로구")
    private String relatedTourSigunguName;

    @Schema(description = "카테고리(대)", example = "관광지")
    private String relatedTourCategoryLarge;

    @Schema(description = "카테고리(소)", example = "시장")
    private String relatedTourCategorySmall;
}