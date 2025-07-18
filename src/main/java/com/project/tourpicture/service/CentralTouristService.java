package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.CentralTouristInfo;
import com.project.tourpicture.repository.CentralTouristInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    String MobileOS = "WEB";
    String MobileApp = "One-cut-travel";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    CentralTouristInfoRepository CTIRepository;

    public CentralTouristService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // 중심 관광지 정보 조회
    public List<CentralTouristInfo> getCentralTouristInfo(String areaCd,
                                                          String signguCd) {
        List<CentralTouristInfo> ctiList = CTIRepository.findByAreaCdAndSignguCdOrderByHubRankAsc(areaCd, signguCd);

        // 오늘 기준 -1개월 (ex: 2025-07-18 → 기준은 202506)
        String baseMonth = LocalDate.now()
                .minusMonths(1)
                .format(DateTimeFormatter.ofPattern("yyyyMM"));

        // 데이터가 없거나, 모든 baseYm이 기준보다 이전이면 업데이트
        boolean shouldUpdate = ctiList.isEmpty() ||
                ctiList.stream().allMatch(info -> info.getBaseYm().compareTo(baseMonth) < 0);


        if (shouldUpdate) {
            fetchCentralTouristInfo(areaCd, signguCd);
            ctiList = CTIRepository.findByAreaCdAndSignguCdOrderByHubRankAsc(areaCd, signguCd);
        }

        return ctiList;
    }

    public void fetchCentralTouristInfo(String areaCd,
                                        String signguCd) {
        try {
            String day = LocalDate.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(1)
                    .format(DateTimeFormatter.ofPattern("yyyyMM"));

            String url = "http://apis.data.go.kr/B551011/LocgoHubTarService1/areaBasedList1"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=" + 1
                    + "&numOfRows=" + 10
                    + "&baseYm=" + day
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&areaCd=" + areaCd
                    + "&signguCd=" + signguCd
                    + "&_type=json";

            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            String resultCode = header.path("resultCode").asText();
            String resultMsg = header.path("resultMsg").asText();

            log.info("중심 관광지 API 응답 헤더: resultCode={}, resultMsg={}", resultCode, resultMsg);

            JsonNode totalCountNode = root.path("response").path("body").path("totalCount");
            int totalCount = totalCountNode.asInt();

            String updateUrl = "http://apis.data.go.kr/B551011/LocgoHubTarService1/areaBasedList1"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=" + 1
                    + "&numOfRows=" + totalCount
                    + "&baseYm=" + day
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&areaCd=" + areaCd
                    + "&signguCd=" + signguCd
                    + "&_type=json";


            URI updateUri = URI.create(updateUrl);
            String updateResponse = restTemplate.getForObject(updateUri, String.class);

            JsonNode updateRoot = objectMapper.readTree(updateResponse);

            header = updateRoot.path("response").path("header");
            resultCode = header.path("resultCode").asText();
            resultMsg = header.path("resultMsg").asText();

            log.info("중심 관광지 API 응답 헤더: resultCode={}, resultMsg={}", resultCode, resultMsg);

            JsonNode itemArray = updateRoot.path("response").path("body").path("items").path("item");

            CTIRepository.saveAll(objectMapper
                    .readerForListOf(CentralTouristInfo.class)
                    .readValue(itemArray));

        } catch (IOException e) {
            log.error("중싱 관광지 정보 조회 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
