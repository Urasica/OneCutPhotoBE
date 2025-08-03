package com.project.tourpicture.controller;

import com.project.tourpicture.dao.CrowdBasedPhoto;
import com.project.tourpicture.service.CrowdBasedPhotoService;
import com.project.tourpicture.service.GovernmentVisitService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crowd-photo")
public class CrowdBasedPhotoController {

    @Autowired
    private final CrowdBasedPhotoService photoService;

    @Autowired
    private final GovernmentVisitService governmentVisitService;
    @Autowired
    private CrowdBasedPhotoService crowdBasedPhotoService;

    @PostMapping("/recommend")
    public ResponseEntity<List<CrowdBasedPhoto>> getPhotos(
            @RequestBody(required = false) Set<String> seenIds,
            @RequestParam(defaultValue = "10") int limit) {

        List<CrowdBasedPhoto> photos = photoService.getCrowdBasedPhotos(seenIds, limit);
        return ResponseEntity.ok(photos);
    }

    @Operation(
            summary = "방문객 및 관광지 목록 업데이트 (임시 배치)",
            description = "절대 절대 사용 금지"
    )
    @GetMapping("/update-visit")
    public String updateVisit() {
        governmentVisitService.VisitTouristUpdate();

        return "OK";
    }

    @Operation(
            summary = "홈 사진 목록 업데이트 (임시 배치)",
            description = "사용 금지"
    )
    @GetMapping("/cache-visit")
    public String cacheVisit() {
        crowdBasedPhotoService.initializePhotoDB();

        return "OK";
    }
}
