package com.project.tourpicture.service;

import com.project.tourpicture.repository.BasicLocalGovernmentFocusInfoRepository;
import com.project.tourpicture.repository.MetropolitanLocalGovernmentFocusInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class AreaService {
    private final BasicLocalGovernmentFocusInfoRepository basicLocalRepository;
    private final MetropolitanLocalGovernmentFocusInfoRepository MetropolitanLocalRepository;

    public List<AreaCodeDto> getAreaCodes() {
        return MetropolitanLocalRepository.findDistinctAreaCodeAndName()
                .stream()
                .map(obj -> new AreaCodeDto((String) obj[0], (String) obj[1]))
                .toList();
    }

    public List<SignguCodeDto> getSignguCodesWithNameByAreaCode(String areaCode) {
        return basicLocalRepository.findDistinctSignguCodeAndNameByAreaCodePrefix(areaCode)
                .stream()
                .map(obj -> new SignguCodeDto((String) obj[0], (String) obj[1]))
                .toList();
    }

    public record AreaCodeDto(String areaCode, String areaName) {}
    public record SignguCodeDto(String signguCode, String signguName) {}
}

