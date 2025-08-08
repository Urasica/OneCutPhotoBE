package com.project.tourpicture.controller;

import com.project.tourpicture.dao.LclsSystemCd;
import com.project.tourpicture.service.LclsSystemCdService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lclsSystemCd")
@RequiredArgsConstructor
public class LclsSystemCdController {
    private final LclsSystemCdService lclsSystemCdService;

    @Operation(
            summary = "분류 코드 목록 조회",
            description = "분류 코드 목록을 조회합니다."
    )
    @GetMapping
    public List<LclsSystemCd> getLclsSystemNm(){
        return lclsSystemCdService.getAllLclsSystemCd();
    }
}
