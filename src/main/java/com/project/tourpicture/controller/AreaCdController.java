package com.project.tourpicture.controller;

import com.project.tourpicture.dto.AreaDTO;
import com.project.tourpicture.service.AreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping("/areas")
    public ResponseEntity<List<AreaDTO>> getAreas() {
        List<AreaDTO> areas = areaService.findAllAreaCodes();

        if (areas == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(areas);
    }
}
