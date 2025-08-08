package com.project.tourpicture.controller;

import com.project.tourpicture.service.*;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/batch")
@RequiredArgsConstructor
public class AdminBatchController {

    private final AreaService areaService;

    private final GovernmentVisitService governmentVisitService;

    private final CrowdBasedPhotoService crowdBasedPhotoService;

    private final LocalGovernmentFocusService localGovernmentFocusService;

    private final LclsSystemCdService lclsSystemCdService;

    @Operation(
            summary = "시도, 시군구 코드 업데이트"
    )
    @PostMapping("/update-codes")
    public void updateCode() {
        areaService.fetchAreaCd();
        areaService.fetchSigunguCd();
    }

    @Operation(
            summary = "방문객 집중률 합산"
    )
    @PostMapping("/update-government-visit")
    public String updateVisit() {
        governmentVisitService.VisitTouristUpdate();

        return "OK";
    }

    @Operation(
            summary = "홈 사진 목록 업데이트"
    )
    @PostMapping("/cache-crowd-based-photo")
    public String cacheVisit() {
        crowdBasedPhotoService.initializePhotoDB();

        return "OK";
    }

    @Operation(
            summary = "광역지자체 집중률 업데이트"
    )
    @PostMapping("/update-metropolitan")
    public String fetchMetropolitanLocalGovernmentFocus() {
        localGovernmentFocusService.fetchMetropolitanLocalGovernmentFocus();
        return "OK";
    }

    @Operation(
            summary = "기초지자체 집중률 업데이트"
    )
    @PostMapping("/update-basic")
    public String fetchBasicLocalGovernmentFocus() {
        localGovernmentFocusService.fetchBasicLocalGovernmentFocus();
        return "OK";
    }

    @Operation(
            summary = "분류코드 목록 업데이트"
    )
    @PostMapping("/update-lclys")
    public String updateLclsSystemCd() {
        lclsSystemCdService.fetchLclsSystemCd();
        return "OK";
    }
}

