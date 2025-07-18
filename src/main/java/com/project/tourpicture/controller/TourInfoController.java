package com.project.tourpicture.controller;

import com.project.tourpicture.dto.RelatedTourRequestDTO;
import com.project.tourpicture.dto.RelatedTourResponseDTO;
import com.project.tourpicture.dto.TourPhotoDTO;
import com.project.tourpicture.service.SpacingService;
import com.project.tourpicture.service.TourInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tour")
public class TourInfoController {

    private final TourInfoService tourInfoService;
    private final SpacingService spacingService;

    //관광지별 연관관광지 리스트 조회
    @GetMapping("/related-list")
    public ResponseEntity<List<RelatedTourResponseDTO>> getRelatedTours(@RequestBody RelatedTourRequestDTO dto) {
        return ResponseEntity.ok(tourInfoService.getRelatedTours(dto));
    }

    //관광지 사진 리스트 조회
    @GetMapping("/photos")
    public ResponseEntity<List<TourPhotoDTO>> getTourPhotos(
            @RequestParam int numOfRows,
            @RequestParam String keyword) {
        return ResponseEntity.ok(tourInfoService.getTourPhotos(numOfRows, keyword));
    }
}
