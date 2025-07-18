package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.TourismFocusInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
public class TourismFocusInfoService {
    @Value("${api.key}")
    private String apiKey;
    private final String MobileOS = "WEB";
    private final String MobileApp = "One-cut-travel";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public TourismFocusInfoService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 관광지 집중률
    public List<TourismFocusInfo> getTourismFocus(String pageNo,
                                                  String numOfRows,
                                                  String areaCd,
                                                  String signguCd) {
        try {
            String url = "http://apis.data.go.kr/B551011/TatsCnctrRateService/tatsCnctrRatedList"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=" + pageNo
                    + "&numOfRows=" + numOfRows
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&areaCd=" + areaCd
                    + "&signguCd=" + signguCd
                    + "&_type=json";


            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            log.info("관광지 집중률 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            return objectMapper
                    .readerForListOf(TourismFocusInfo.class)
                    .readValue(itemArray);
        } catch (IOException e) {
            log.error("관광지 정보 조회 중 예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }

    // 관광지 집중률 조회 (관광지명 포함)
    public List<TourismFocusInfo> getTourismFocusByName(String pageNo,
                                                        String numOfRows,
                                                        String areaCd,
                                                        String signguCd,
                                                        String tAtsNm) {


        try {
            String encodedName = URLEncoder.encode(tAtsNm, StandardCharsets.UTF_8);
            String url = "https://apis.data.go.kr/B551011/TatsCnctrRateService/tatsCnctrRatedList"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=" + pageNo
                    + "&numOfRows=" + numOfRows
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&areaCd=" + areaCd
                    + "&signguCd=" + signguCd
                    + "&tAtsNm=" + encodedName
                    + "&_type=json";


            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            log.info("관광지 집중률(관광지 포함) API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            return objectMapper
                    .readerForListOf(TourismFocusInfo.class)
                    .readValue(itemArray);
        } catch (IOException e) {
            log.error("관광지 정보 조회 중 예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}
