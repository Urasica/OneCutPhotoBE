package com.project.tourpicture.controller;

import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.service.AreaService;
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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AreaCdController {
    private final AreaService areaService;

    @Operation(
            summary = "시도 코드 조회",
            description = "시도 코드들을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/areas")
    public ResponseEntity<List<AreaService.AreaCodeDto>> getAreas() {
        List<AreaService.AreaCodeDto> areas = areaService.getAreaCodes();

        if (areas == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(areas);
    }

    @Operation(
            summary = "시군구 코드 조회",
            description = "시도 단위로 시군구 코드를 조회합니다. (시도 코드는 시도 코드 조회 참조)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "정상 응답"),
            @ApiResponse(responseCode = "404", description = "데이터 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/sigungus")
    public ResponseEntity<List<AreaService.SigunguCodeDto>> getSigungus(@Parameter(description = "지역 코드", example = "11") @RequestParam String areaCd) {
        List<AreaService.SigunguCodeDto> sigungus = areaService.getSignguCodesWithNameByAreaCode(areaCd);

        if (sigungus == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(sigungus);
    }

    @Operation(
            summary = "시도, 시군구 코드 업데이트(임시 배치)",
            description = "사용 금지"
    )
    @GetMapping("/update-code")
    public void updateCode() {
        areaService.fetchAreaCd();
        areaService.fetchSigunguCd();
    }
}
