package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.BasicLocalGovernmentFocusInfo;
import com.project.tourpicture.dao.MetropolitanLocalGovernmentFocusInfo;
import com.project.tourpicture.repository.BasicLocalGovernmentFocusInfoRepository;
import com.project.tourpicture.repository.MetropolitanLocalGovernmentFocusInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class LocalGovernmentFocusService {
    @Value("${api.key}")
    private String apiKey;
    private final String MobileOS = "WEB";
    private final String MobileApp = "One-cut-travel";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Autowired
    MetropolitanLocalGovernmentFocusInfoRepository MGLFIRepo;
    @Autowired
    BasicLocalGovernmentFocusInfoRepository BGLFIRepo;

    public LocalGovernmentFocusService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 광역지자체 집중률 반환
    public List<MetropolitanLocalGovernmentFocusInfo> getMetropolitanLocalGovernmentFocus() {
        return MGLFIRepo.findAll();
    }

    // 광역지자체 집중률 조회
    @Transactional
    public void fetchMetropolitanLocalGovernmentFocus() {
        try {
            String day = LocalDate.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(2)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String url = "https://apis.data.go.kr/B551011/DataLabService/metcoRegnVisitrDDList"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=51"
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&startYmd=" + day
                    + "&endYmd=" + day
                    + "&_type=json";


            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            log.info("광역지자체 집중률 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            List<MetropolitanLocalGovernmentFocusInfo> dataList =
                    objectMapper.readerForListOf(MetropolitanLocalGovernmentFocusInfo.class)
                            .readValue(itemArray);

            MGLFIRepo.deleteAll();
            MGLFIRepo.saveAll(dataList);

        } catch (IOException e) {
            log.error("광역지자체 집중률 정보 조회 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    // 기초 지자체 집중률 반환
    public List<BasicLocalGovernmentFocusInfo> getBasicLocalGovernmentFocus(String areaCd) {
        return BGLFIRepo.findBySigunguCdStartingWith(areaCd);
    }

    // 기초 지자체 집중률 조회
    public void fetchBasicLocalGovernmentFocus() {
        try {
            String day = LocalDate.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(2)
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            String url = "https://apis.data.go.kr/B551011/DataLabService/locgoRegnVisitrDDList"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=740"
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&startYmd=" + day
                    + "&endYmd=" + day
                    + "&_type=json";


            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            log.info("기초지자체 집중률 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            List<BasicLocalGovernmentFocusInfo> dataList =
                    objectMapper.readerForListOf(BasicLocalGovernmentFocusInfo.class)
                            .readValue(itemArray);

            BGLFIRepo.deleteAll();
            BGLFIRepo.saveAll(dataList);

        } catch (IOException e) {
            log.error("기초지자체 집중률 정보 조회 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
