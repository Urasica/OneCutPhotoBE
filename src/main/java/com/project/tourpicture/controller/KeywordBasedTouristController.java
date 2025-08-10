package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.dto.KeywordBasedTouristDTO;
import com.project.tourpicture.service.KeywordBasedTouristService;
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
@RequestMapping("/api/keyword-base")
@RequiredArgsConstructor
@Slf4j
public class KeywordBasedTouristController {
    private final KeywordBasedTouristService keywordBasedTouristService;

    @Operation(
            summary = "키워드 기반 관광지 조회",
            description = "키워드로 관광지를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/tourist-attractions")
    public ResponseEntity<List<KeywordBasedTouristDTO>> getKeywordBasedTourist(@Parameter(description = "키워드", example = "시장") @RequestParam String keyword) {
        log.info("GET /api/keyword-base/tourist-attractions 요청: keyword={}", keyword);

        List<KeywordBasedTouristDTO> response;

        response = keywordBasedTouristService.getKeywordBasedTourists(keyword);

        if (response == null || response.isEmpty()) {
            log.warn("키워드 기반 관광지 데이터 없음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        log.info("키워드 기반 관광지 {}건 반환", response.size());
        return ResponseEntity.ok(response);
    }
}
