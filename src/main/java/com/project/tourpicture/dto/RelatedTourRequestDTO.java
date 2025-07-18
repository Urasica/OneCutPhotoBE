package com.project.tourpicture.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RelatedTourRequestDTO {
    private int numOfRows;
    private String baseYm;
    private String areaCode;
    private String sigunguCode;
    private String keyword;
}
