package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.dto.RelatedTourDTO;
import com.project.tourpicture.service.RelatedTourService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.project.tourpicture.util.AppUtils.getErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tour")
public class RelatedTourController {

    private final RelatedTourService relatedTourService;

    @Operation(summary = "관광지 연관 리스트 조회",
            description = "입력한 관광지 키워드 기준으로 관련 관광지 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RelatedTourDTO.class))),
            @ApiResponse(responseCode = "502", description = "파싱 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/related-list")
    public ResponseEntity<?> getRelatedTouristSpots(
            @Parameter(description = "시도코드", example = "11") @RequestParam String areaCd,
            @Parameter(description = "시군구코드", example = "110") @RequestParam String sigunguCd,
            @Parameter(description = "컨텐츠 ID", example = "1019041") @RequestParam String contentId) {
        try {
            return ResponseEntity.ok(relatedTourService.getRelatedTouristSpots(areaCd, sigunguCd, contentId));
        } catch (Exception e) {
            return getErrorResponse(e);
        }
    }
}
