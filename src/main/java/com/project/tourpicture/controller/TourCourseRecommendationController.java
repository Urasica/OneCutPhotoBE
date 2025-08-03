package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.dto.TourCourseDTO;
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

import java.util.List;

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
                            schema = @Schema(implementation = TourCourseDTO.class))),
            @ApiResponse(responseCode = "404", description = "코스 추천 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/recommend-courses-distance")
    public ResponseEntity<?> getCoursesByDistance(
            @Parameter(description = "관광지명", example = "경복궁")@RequestParam String startSpot,
            @Parameter(description = "지역코드", example = "11")@RequestParam String areaCd,
            @Parameter(description = "시군구코드", example = "11110")@RequestParam String sigunguCode) {
        try {
            return ResponseEntity.ok(tourCourseRecommendationService.getCourseByDistance(startSpot, areaCd, sigunguCode, 4));
        } catch (Exception ex){
            return TourInfoController.getErrorResponse(ex);
        }
    }

    @Operation(summary = "관광지별 코스 추천(방문률 우선)",
            description = "입력한 관광지를 시작으로 방문률을 우선으로 고려한 코스를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TourCourseDTO.class))),
            @ApiResponse(responseCode = "404", description = "코스 추천 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/recommend-courses-popularity")
    public ResponseEntity<?> getCoursesByPopularity(
            @Parameter(description = "관광지명", example = "경복궁")@RequestParam String startSpot,
            @Parameter(description = "지역코드", example = "11")@RequestParam String areaCd,
            @Parameter(description = "시군구코드", example = "11110")@RequestParam String sigunguCode) {
        try {
            return ResponseEntity.ok(tourCourseRecommendationService.getCourseByPopularity(startSpot, areaCd, sigunguCode, 4));
        } catch (Exception ex) {
            return TourInfoController.getErrorResponse(ex);
        }
    }

    @Operation(summary = "관광지별 코스 추천(거리, 방문률 모두 고려)",
            description = "입력한 관광지를 시작으로 거리, 방문률을 모두 고려한 코스를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TourCourseDTO.class))),
            @ApiResponse(responseCode = "404", description = "코스 추천 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/recommend-courses-distance-And-popularity")
    public ResponseEntity<?> getCoursesByDistanceAndPopularity(
            @Parameter(description = "관광지명", example = "경복궁")@RequestParam String startSpot,
            @Parameter(description = "지역코드", example = "11")@RequestParam String areaCd,
            @Parameter(description = "시군구코드", example = "11110")@RequestParam String sigunguCode) {
        try {
            return ResponseEntity.ok(tourCourseRecommendationService.getCourseByDistanceAndPopularity(startSpot, areaCd, sigunguCode, 4));
        } catch (Exception ex){
            return TourInfoController.getErrorResponse(ex);
        }
    }

}
