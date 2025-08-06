package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "모든 관광지 카테고리 공통 정보")
public class TourCommonInfoDTO {

    @Schema(description = "콘텐츠 ID", example = "126128")
    private String contentId;

    @Schema(description = "관광타입 ID", example = "12")
    private String contentTypeId;

    @Schema(description = "관광지명", example = "동촌유원지")
    private String name;

    @Schema(description = "관광지 이미지", example = "http://tong.visitkorea.or.kr/cms/resource/86/3488286_image2_1.JPG")
    private String imageUrl;

    @Schema(description = "홈페이지 주소", example = "https://tour.daegu.go.kr/index.do?menu_id=00002942&menu_link=/front/tour/tourMapsView.do?tourId=KOATTR_115")
    private String homepage;

    @Schema(description = "주소", example = "대구광역시 동구 효목동")
    private String address1;

    @Schema(description = "상세 주소", example = "산 234-29")
    private String address2;

    @Schema(description = "우편 번호", example = "41179")
    private String zipcode;

    @Schema(description = "X 좌표값", example = "128.6506352387")
    private String mapX;

    @Schema(description = "Y 좌표값", example = "35.8826195757")
    private String mapY;

    @Schema(description = "개요", example = "동촌유원지는 대구시 동쪽 금호강변에 있는 44만 평의 유원지로...")
    private String overview;
}
