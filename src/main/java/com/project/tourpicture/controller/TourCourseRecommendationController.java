package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.dto.TourCourseItemDTO;
import com.project.tourpicture.service.TourCourseRecommendationService;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tour")
public class TourCourseRecommendationController {

    private final TourCourseRecommendationService tourCourseRecommendationService;

    @Operation(summary = "관광지별 코스 추천(거리 우선)",
            description = "입력한 관광지를 시작으로 거리를 우선으로 고려한 코스를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TourCourseItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "코스 추천 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/recommend-course-distance")
    public ResponseEntity<?> getCourseByDistance(
            @Parameter(description = "시도코드", example = "11")@RequestParam String areaCd,
            @Parameter(description = "시군구코드", example = "110")@RequestParam String sigunguCd,
            @Parameter(description = "컨텐츠 ID", example = "2993699")@RequestParam String contentId,
            @Parameter(description = "관광지명", example = "청와대 전망대") @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(tourCourseRecommendationService.getCourseByDistance(
                    areaCd, sigunguCd, contentId, keyword,4));
        } catch (Exception e){
            return getErrorResponse(e);
        }
    }
}
