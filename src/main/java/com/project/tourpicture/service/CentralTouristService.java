package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.CentralTouristInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class CentralTouristService {
    @Value("${api.key}")
    private String apiKey;
    private final String MobileOS = "WEB";
    private final String MobileApp = "One-cut-travel";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CentralTouristService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 중심 관광지 정보 조회
    public List<CentralTouristInfo> getCentralTouristInfo(String pageNo,
                                                          String numOfRows,
                                                          String areaCd,
                                                          String signguCd) {
        try {
            String day = LocalDate.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMM"));

            String url = "http://apis.data.go.kr/B551011/LocgoHubTarService1/areaBasedList1"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=" + pageNo
                    + "&numOfRows=" + numOfRows
                    + "&baseYm=" + day
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&areaCd=" + areaCd
                    + "&signguCd=" + signguCd
                    + "&_type=json";


            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            System.out.println(response);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            String resultCode = header.path("resultCode").asText();
            String resultMsg = header.path("resultMsg").asText();

            log.info("중심 관광지 API 응답 헤더: resultCode={}, resultMsg={}", resultCode, resultMsg);

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            return objectMapper
                    .readerForListOf(CentralTouristInfo.class)
                    .readValue(itemArray);
        } catch (IOException e) {
            log.error("중싱 관광지 정보 조회 중 예외 발생: {}", e.getMessage(), e);
            return null;
        }
    }
}
