package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.dto.RelatedTourResponseDTO;
import com.project.tourpicture.dto.TourPhotoDTO;
import com.project.tourpicture.service.TourInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tour")
public class TourInfoController {

    private final TourInfoService tourInfoService;

    @Operation(summary = "관광지 연관 리스트 조회",
            description = "입력한 관광지 키워드 기준으로 관련 관광지 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RelatedTourResponseDTO.class))),
            @ApiResponse(responseCode = "502", description = "파싱 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/related-list")
    public ResponseEntity<?> getRelatedTours(
            @Parameter(description = "조회할 개수", example = "10") @RequestParam int numOfRows,
            @Parameter(description = "기준 연월 ", example = "202506") @RequestParam String baseYm,
            @Parameter(description = "지역 코드", example = "11") @RequestParam String areaCode,
            @Parameter(description = "시군구 코드", example = "11110") @RequestParam String sigunguCode,
            @Parameter(description = "관광지명", example = "경복궁") @RequestParam String keyword) {
        try {
            return ResponseEntity.ok(tourInfoService.getRelatedTours(numOfRows, baseYm, areaCode, sigunguCode, keyword));
        } catch (ResponseStatusException ex) {
            return getErrorResponseResponseEntity(ex);
        }
    }

    @Operation(summary = "관광지 대표 사진 조회",
            description = "키워드로 관광지 대표 사진과 촬영 연월 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TourPhotoDTO.class))),
            @ApiResponse(responseCode = "404", description = "이미지 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/tour-photos")
    public ResponseEntity<?> getTourPhotos(
            @Parameter(description = "관광지명", example = "경복궁") @RequestParam String keyword) {
        try {
            TourPhotoDTO photo = tourInfoService.getTourPhoto(keyword);
            return ResponseEntity.ok(photo);
        } catch (ResponseStatusException ex) {
            return getErrorResponseResponseEntity(ex);
        }
    }

    private static ResponseEntity<ErrorResponse> getErrorResponseResponseEntity(ResponseStatusException ex) {
        ErrorResponse error = new ErrorResponse(ex.getStatusCode().value(), ex.getReason());
        return ResponseEntity.status(ex.getStatusCode().value()).body(error);
    }
}
