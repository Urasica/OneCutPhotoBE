package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "집중도 기반 관광지 사진 DTO")
public class CrowdBasedPhotoResponseDTO {
    private Long photoId;

    @Schema(description = "시군구 코드", example = "11530")
    private String signguCd;

    @Schema(description = "시군구 명", example = "구로구")
    private String signguNm;

    @Schema(description = "중심 관광지 코드")
    private String hubTatsCd;

    @Schema(description = "중심 관광지 명")
    private String hubTatsNm;

    @Schema(description = "관광지 카테고리")
    private String hubCtgryLclsNm;

    @Schema(description = "관광지 이미지 URL")
    private String imageUrl;

    @Schema(description = "이미지 수정 월", example = "202407")
    private String takenMonth;
}
