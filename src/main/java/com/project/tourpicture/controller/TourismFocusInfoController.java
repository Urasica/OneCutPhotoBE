package com.project.tourpicture.controller;

import com.project.tourpicture.dao.TourismFocusInfo;
import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.service.TourismFocusInfoService;
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
public class TourismFocusInfoController {
    @Autowired
    TourismFocusInfoService tfService;

    @Operation(
            summary = "관광지 집중률 조회",
            description = "관광지명(tAtsNm)을 포함하면 해당 관광지만, 포함하지 않으면 전체 관광지 집중률을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/tourism-focus")
public ResponseEntity<List<TourismFocusInfo>> getTourismFocusInfo(@Parameter(description = "페이지 번호", example = "1") @RequestParam String pageNo,
                                                                  @Parameter(description = "한 페이지 결과 수", example = "10") @RequestParam String numOfRows,
                                                                  @Parameter(description = "지역 코드", example = "11") @RequestParam String areaCd,
                                                                  @Parameter(description = "시군구 코드", example = "11110") @RequestParam String signguCd,
                                                                  @Parameter(description = "관광지명 (선택)", example = "경복궁") @RequestParam(required = false) String tAtsNm) {

        log.info("GET /api/tourism-focus 요청: pageNo={}, numOfRows={}, areaCd={}, sigunguCd={}, tAtsNm={}",
                pageNo, numOfRows, areaCd, signguCd, tAtsNm);

        List<TourismFocusInfo> response;

        if (tAtsNm != null && !tAtsNm.isBlank()) {
            // 관광지명 검색 전용 메서드 호출
            response = tfService.getTourismFocusByName(pageNo, numOfRows, areaCd, signguCd, tAtsNm);
        } else {
            // 전체 조회
            response = tfService.getTourismFocus(pageNo, numOfRows, areaCd, signguCd);
        }

        if (response == null || response.isEmpty()) {
            log.warn("관광지 혼잡도 데이터 없음 → 404 반환");
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        log.info("관광지 혼잡도 {}건 반환", response.size());
        return ResponseEntity.ok(response);
    }
}
