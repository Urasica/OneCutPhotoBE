package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.dto.TourCommonInfoDTO;
import com.project.tourpicture.service.TourCommonInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.project.tourpicture.util.AppUtils.getErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tour")
public class TourInfoController {

    private final TourCommonInfoService tourCommonInfoService;

    @Operation(summary = "모든 관광지 카테고리 공통 정보 조회(관광타입 ID, 홈페이지, 주소, 이미지, 개요 등)",
            description = "입력한 콘텐츠 ID에 해당하는 공통 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TourCommonInfoDTO.class))),
            @ApiResponse(responseCode = "502", description = "조회 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/commonInfo")
    public ResponseEntity<?> getTourCommonInfo(
            @Parameter(description = "콘텐츠 ID", example = "126128") @RequestParam String contentId) {
        try {
            return ResponseEntity.ok(tourCommonInfoService.getTourCommonInfo(contentId));
        } catch (Exception e) {
            return getErrorResponse(e);
        }
    }
}
