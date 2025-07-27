package com.project.tourpicture.controller;

import com.project.tourpicture.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AreaCdController {
    private final AreaService areaService;

    @GetMapping("update")
    public String update() {
        areaService.fetchAndSaveAllAreas();

        return "success";
    }
}
