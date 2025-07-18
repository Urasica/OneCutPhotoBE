package com.project.tourpicture.controller;

import com.project.tourpicture.dao.CentralTouristInfo;
import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.service.CentralTouristService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class CentralTouristController {
    @Autowired
    CentralTouristService ctService;

    @Operation(
            summary = "중심 관광지 조회",
            description = "시군구 단위로 중심 관광지를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/center-tourist")
    public ResponseEntity<List<CentralTouristInfo>> getCentralTourist(@Parameter(description = "지역 코드", example = "11") @RequestParam String areaCd,
                                                                      @Parameter(description = "시군구 코드", example = "11530") @RequestParam String signguCd) {

        log.info("GET /api/tourism-focus 요청: areaCd={}, signguCd={}", areaCd, signguCd);

        List<CentralTouristInfo> response;

        response = ctService.getCentralTouristInfo(areaCd, signguCd);

        if (response == null || response.isEmpty()) {
            log.warn("중심 관광지 데이터 없음 → 404 반환");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        log.info("중심 관광지 {}건 반환", response.size());
        return ResponseEntity.ok(response);
    }
}
