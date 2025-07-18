package com.project.tourpicture.controller;

import com.project.tourpicture.dao.BasicLocalGovernmentFocusInfo;
import com.project.tourpicture.dao.MetropolitanLocalGovernmentFocusInfo;
import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.service.LocalGovernmentFocusService;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class LocalGovernmentFocusController {
    @Autowired
    LocalGovernmentFocusService lgfService;

    @Operation(
            summary = "광역지자체 집중률 조회",
            description = "광역지자체 관광지 집중률를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/metropolitan-localgovernment-focus")
    public ResponseEntity<List<MetropolitanLocalGovernmentFocusInfo>> getMetropolitanLocalGovernmentFocus() {
        log.info("GET /api/metropolitan-localgovernment-focus 요청 수신");

        List<MetropolitanLocalGovernmentFocusInfo> result = lgfService.getMetropolitanLocalGovernmentFocus();

        if (result == null || result.isEmpty()) {
            log.warn("광역지자체 집중률 정보 없음 -> 404 반환");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("광역지자체 집중률 정보 {}건 반환", result.size());
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "기초지자체 집중률 조회",
            description = "기초지자체 관광지 집중률를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/basic-localgovernment-focus")
    public ResponseEntity<List<BasicLocalGovernmentFocusInfo>> getBasicLocalGovernmentFocus(@Parameter(description = "시도 코드", example = "11") @RequestParam String areaCd) {
        log.info("GET /api/basic-localgovernment-focus 요청 수신");

        List<BasicLocalGovernmentFocusInfo> result = lgfService.getBasicLocalGovernmentFocus(areaCd);

        if (result == null || result.isEmpty()) {
            log.warn("기초지자체 집중률 정보 없음 -> 404 반환");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("기초지자체 집중률 정보 {}건 반환", result.size());
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "광역지자체 집중률 업데이트 (임시 배치)",
            description = "사용 금지"
    )
    @GetMapping("fetch-metropolitan")
    public String fetchMetropolitanLocalGovernmentFocus() {
        lgfService.fetchMetropolitanLocalGovernmentFocus();
        return "OK";
    }

    @Operation(
            summary = "기초지자체 집중률 업데이트 (임시 배치)",
            description = "사용 금지"
    )
    @GetMapping("fetch-basic")
    public String fetchBasicLocalGovernmentFocus() {
        lgfService.fetchBasicLocalGovernmentFocus();
        return "OK";
    }
}
