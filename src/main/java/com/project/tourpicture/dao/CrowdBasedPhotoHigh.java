package com.project.tourpicture.dao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrowdBasedPhotoHigh {
    @Id
    @Schema(description = "콘텐츠 Id")
    private String contentId;

    @Schema(description = "관광지 명")
    private String title;

    @Schema(description = "주소")
    private String addr1;

    @Schema(description = "상세 주소")
    private String addr2;

    @Schema(description = "시도 코드")
    private String areaCd;

    @Schema(description = "시군구 코드")
    private String sigunguCd;

    @Schema(description = "시군구 명", example = "구로구")
    private String sigunguNm;

    @Schema(description = "관광지 이미지 URL")
    private String imageUrl;

    @Schema(description = "저작권 Type1: 출처표시-권장, Type3: 출처표시 + 변경금지")
    private String cpyrhtDivCd;

    @Schema(description = "이미지 수정일")
    private String modifiedTime;

    @Schema(description = "컨텐츠 타입 Id")
    private String contentTypeId;

    @Schema(description = "X 좌표")
    private String mapX;

    @Schema(description = "Y 좌표")
    private String mapY;
}
