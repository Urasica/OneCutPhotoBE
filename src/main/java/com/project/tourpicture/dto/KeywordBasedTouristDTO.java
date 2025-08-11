package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class KeywordBasedTouristDTO {
    @Schema(description = "주소")
    private String addr1;

    @Schema(description = "상세 주소")
    private String addr2;

    @Schema(description = "콘텐츠 ID")
    private String contentId;

    @Schema(description = "관광타입(12: 관광지, 14: 문화시설, 15: 축제공연행사, 25: 여행코스, 28: 레포츠, 32: 숙박, 38: 쇼핑, 39: 음식점)")
    private String contentTypeId;

    @Schema(description = "생성일")
    private String createdTime;

    @Schema(description = "수정일")
    private String modifiedTime;

    @Schema(description = "이미지1")
    private String firstImage;

    @Schema(description = "이미지2")
    private String firstImage2;

    @Schema(description = "저작권 Type1: 출처표시-권장, Type3: 출처표시 + 변경금지")
    private String cpyrhtDivCd;

    @Schema(description = "GPS x좌표")
    private String mapX;

    @Schema(description = "GPS y좌표")
    private String mapY;

    @Schema(description = "Map Level")
    private String mlevel;

    @Schema(description = "관광지명")
    private String title;

    @Schema(description = "우편주소")
    private String zipcode;

    @Schema(description = "분류 체계 명")
    private String lclsSystemNm;

    @Schema(description = "시도 코드")
    private String areaCd;

    @Schema(description = "시군구 코드")
    private String sigunguCd;
}
