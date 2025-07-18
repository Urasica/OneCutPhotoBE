package com.project.tourpicture.controller;

import com.project.tourpicture.dto.RelatedTourRequestDTO;
import com.project.tourpicture.dto.RelatedTourResponseDTO;
import com.project.tourpicture.dto.TourPhotoDTO;
import com.project.tourpicture.service.TourInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tour")
public class TourInfoController {

    private final TourInfoService tourInfoService;

    @Operation(summary = "관광지 연관 리스트 조회", description = "입력한 관광지 키워드 기준으로 관련 관광지 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 연관 관광지 리스트 반환"),
            @ApiResponse(responseCode = "400", description = "요청 값이 잘못됨"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/related-list")
    public ResponseEntity<List<RelatedTourResponseDTO>> getRelatedTours(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "조회개수, 시기(예:202506), 지역코드, 시군구코드, 관광지명")
            @RequestBody RelatedTourRequestDTO dto) {
        return ResponseEntity.ok(tourInfoService.getRelatedTours(dto));
    }

    @Operation(summary = "관광지 대표 사진 조회", description = "키워드로 관광지 대표 사진과 촬영 시지 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 대표 이미지 반환"),
            @ApiResponse(responseCode = "400", description = "파라미터 누락 또는 잘못된 값"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/photos")
    public ResponseEntity<TourPhotoDTO> getTourPhotos(
            @Parameter(description = "관광지 검색 키워드") @RequestParam String keyword) {
        return ResponseEntity.ok(tourInfoService.getTourPhotos(keyword));
    }
}
