package com.project.tourpicture.controller;

import com.project.tourpicture.dto.AccommodationInfoDTO;
import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.service.AccommodationInfoService;
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
public class AccommodationInfoController {

    private final AccommodationInfoService accommodationInfoService;

    @Operation(summary = "관광지 숙소 정보 제공",
            description = "입력한 시도 및 시군구 코드 기준으로 숙소 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccommodationInfoDTO.class))),
            @ApiResponse(responseCode = "502", description = "파싱 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "조회 실패",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/accommodation")
    public ResponseEntity<?> getAccommodations(
            @Parameter(description = "시도코드", example = "51") @RequestParam String areaCd,
            @Parameter(description = "시군구코드", example = "820") @RequestParam String sigunguCd,
            @Parameter(description = "조회할 개수", example = "10") @RequestParam int numOfRows) {
        try {
            return ResponseEntity.ok(accommodationInfoService.getAccommodations(areaCd, sigunguCd, numOfRows));
        } catch (Exception e) {
            return getErrorResponse(e);
        }
    }
}
