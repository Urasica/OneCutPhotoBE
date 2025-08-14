package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.dto.RegionBasedTouristDTO;
import com.project.tourpicture.service.RegionBasedTouristService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/region-base")
@RequiredArgsConstructor
@Slf4j
public class RegionBasedTouristController {
    private final RegionBasedTouristService regionBasedTouristService;

    @Operation(
            summary = "지역 기반 관광지 조회",
            description = "시군구 단위로 관광지를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/tourist-attractions")
    public ResponseEntity<List<RegionBasedTouristDTO>> getRegionBasedTourist(@Parameter(description = "지역 코드", example = "11") @RequestParam String areaCd,
                                                                             @Parameter(description = "시군구 코드", example = "110") @RequestParam String sigunguCd) {
        log.info("GET /api/region-base/tourist-attractions 요청: areaCd={}, sigunguCd={}", areaCd, sigunguCd);

        List<RegionBasedTouristDTO> response;

        response = regionBasedTouristService.getRegionBasedTourists(areaCd, sigunguCd, 12);
        response.addAll(regionBasedTouristService.getRegionBasedTourists(areaCd, sigunguCd, 14));

        if (response.isEmpty()) {
            log.warn("지역 기반 관광지 데이터 없음 → 404 반환");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        log.info("지역 기반 관광지 {}건 반환", response.size());
        return ResponseEntity.ok(response);
    }
}
