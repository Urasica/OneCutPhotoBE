package com.project.tourpicture.dto.intro;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Schema(description = "쇼핑 카테고리 소개 정보")
public class ShoppingIntroDTO {

    @Schema(description = "애완동물동반가능정보")
    private String chkPet;

    @Schema(description = "문의및안내")
    private String infoCenter;

    @Schema(description = "영업시간")
    private String openTime;

    @Schema(description = "주차시설")
    private String parking;

    @Schema(description = "쉬는날")
    private String restDate;

    @Schema(description = "화장실설명")
    private String restroom;

    @Schema(description = "판매품목")
    private String saleItem;

    @Schema(description = "규모")
    private String scaleShopping;
}
