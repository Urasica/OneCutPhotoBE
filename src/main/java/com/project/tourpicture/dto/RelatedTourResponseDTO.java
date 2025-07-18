package com.project.tourpicture.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class RelatedTourResponseDTO {
    private String relatedTourName;
    private String relatedTourAreaCode;
    private String relatedTourAreaName;
    private String relatedTourSigunguCode;
    private String relatedTourSigunguName;
    private String relatedTourCategoryLarge;
    private String relatedTourCategorySmall;
    private List<TourPhotoDTO> photo;
}