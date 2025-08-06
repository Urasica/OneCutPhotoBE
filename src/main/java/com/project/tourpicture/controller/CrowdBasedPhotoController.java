package com.project.tourpicture.controller;

import com.project.tourpicture.dao.CrowdBasedPhoto;
import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.service.CrowdBasedPhotoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crowd-photo")
public class CrowdBasedPhotoController {

    private final CrowdBasedPhotoService photoService;


    @Operation(
            summary = "홈화면 사진용 관광지 목록 조회",
            description = "요청 시마다 랜덤하게 섞인 관광지 목록을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/recommend")
    public ResponseEntity<List<CrowdBasedPhoto>> getPhotos() {

        List<CrowdBasedPhoto> photos = photoService.getCrowdBasedPhotos();
        return ResponseEntity.ok(photos);
    }
}
