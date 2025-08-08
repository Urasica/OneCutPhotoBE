package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.LclsSystemCd;
import com.project.tourpicture.repository.LclsSystemCdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LclsSystemCdService {
    private final LclsSystemCdRepository lclsSystemCdRepository;

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void fetchLclsSystemCd() {
        try {
            String url = "https://apis.data.go.kr/B551011/KorService2/lclsSystmCode2"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=20"
                    + "&MobileOS=" + "WEB"
                    + "&MobileApp=" + "One-cut-travel"
                    + "&lclsSystm1=&lclsSystm2=&lclsSystm3="
                    + "&lclsSystmListYn=N"
                    + "&_type=json";

            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            log.info("분류 체계 코드 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            List<LclsSystemCd> dataList = objectMapper.readerForListOf(LclsSystemCd.class).readValue(itemArray);

            if(dataList != null && !dataList.isEmpty()) {
                lclsSystemCdRepository.deleteAll();
                lclsSystemCdRepository.saveAll(dataList);
                log.info("분류 체계 코드 API 저장 완료");
            }

        } catch (IOException e) {
            log.error("분류 체계 코드 조회 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    public String searchLclsSystemNm(String lclsSystmCd) {
        String name = lclsSystemCdRepository.findByCode(lclsSystmCd).getName();
        if(name != null && !name.isEmpty()) {
            return name;
        }
        return "";
    }

    public List<LclsSystemCd> getAllLclsSystemCd() {
        return lclsSystemCdRepository.findAll();
    }
}
