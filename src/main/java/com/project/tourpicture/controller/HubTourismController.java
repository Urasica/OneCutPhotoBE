package com.project.tourpicture.controller;

import com.project.tourpicture.dao.HubTourismEntity;
import com.project.tourpicture.service.HubTourismService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HubTourismController {
    private final HubTourismService hubTourismService;

    @GetMapping("/HubTourism")
    public ResponseEntity<List<HubTourismEntity>> getHubTourism(@Parameter(description = "지역 코드", example = "11") @RequestParam String areaCd,
                                                                @Parameter(description = "시군구 코드", example = "110") @RequestParam String sigunguCd) {
        return  ResponseEntity.ok(hubTourismService.getHubTourismWithRanking(areaCd, sigunguCd));
    }
}
