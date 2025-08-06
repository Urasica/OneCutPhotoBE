package com.project.tourpicture.service;

import com.project.tourpicture.dto.RelatedTourDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.project.tourpicture.util.AppUtils.*;

@Service
@RequiredArgsConstructor
public class RelatedTourService {

    private final RestTemplate restTemplate;

    @Value("${api.key}")
    private String serviceKey;

    // 관광지별(키워드) 연관관광지 조회
    public List<RelatedTourDTO> getRelatedTouristSpots(
            int numOfRow, String baseYm, String areaCode, String sigunguCode, String keyword) {

        URI uri = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/B551011/TarRlteTarService1/searchKeyword1")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRow)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("baseYm", baseYm)
                .queryParam("areaCd", areaCode)
                .queryParam("signguCd", areaCode + sigunguCode)
                .queryParam("keyword", UriUtils.encodeQueryParam(keyword, "UTF-8"))
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return parseRelatedTouristSpots(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("외부 API 응답 파싱 오류", e);
        }
    }

    // 연관관광지 응답값 파싱
    private List<RelatedTourDTO> parseRelatedTouristSpots(String json) throws JsonProcessingException {
        JsonNode items = getItemsNode(json);
        List<RelatedTourDTO> results = new ArrayList<>();

        for (JsonNode item : items) {
            RelatedTourDTO dto = new RelatedTourDTO();
            String rlteTatsNm = item.path("rlteTatsNm").textValue(); //관광지명
            String rlteSignguCd = item.path("rlteSignguCd").textValue(); //관광지 시군구코드

            dto.setRelatedTourName(rlteTatsNm);
            dto.setImageUrl(getRelatedTourPhoto(rlteSignguCd, rlteTatsNm));
            dto.setAreaName(item.path("rlteRegnNm").textValue());
            dto.setAreaCd(item.path("rlteRegnCd").textValue());
            dto.setSigunguName(item.path("rlteSignguNm").textValue());
            dto.setSigunguCd(rlteSignguCd.substring(2));
            dto.setRelatedTourCategoryLarge(item.path("rlteCtgryLclsNm").textValue());
            dto.setRelatedTourCategorySmall(item.path("rlteCtgrySclsNm").textValue());

            results.add(dto);
        }
        return results;
    }

    // 연관관광지 대표 사진 조회
    public String getRelatedTourPhoto(String area, String keyword){
        String processedKeyword = (keyword.contains("/") ? keyword.split("/")[0] : keyword).trim();

        URI uri = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/B551011/KorService2/searchKeyword2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", 10)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("arrange", "C")
                .queryParam("keyword", UriUtils.encodeQueryParam(processedKeyword, "UTF-8"))
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return parseRelatedTourPhoto(response.getBody(), area, processedKeyword);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("외부 API 응답 파싱 오류", e);
        }
    }

    // 사진 응답값 파싱
    private String parseRelatedTourPhoto(String json, String area, String keyword) throws JsonProcessingException {
        JsonNode items = getItemsNode(json);

        if(items.isMissingNode() || !items.isArray() || items.isEmpty()) {
            return "이미지 미제공";
        }

        // 관광지명이 일치할 경우
        for (JsonNode item : items) {
            if(item.path("title").asText().equals(keyword)) {
                String url = item.path("firstimage").asText();
                if(url != null && !url.isEmpty()) {
                    return url;
                }
            }
        }

        // 관광지명이 부분 일치할 경우
        for (JsonNode item : items) {
            String lDongRegnCd = item.path("lDongRegnCd").asText();
            String lDongSignguCd = item.path("lDongSignguCd").asText();

            if (item.path("title").asText().contains(keyword) && (lDongRegnCd + lDongSignguCd).equals(area)) {
                String url = item.path("firstimage").asText();
                if (url != null && !url.isEmpty()) {
                    return url;
                }
            }
        }
        return "이미지 미제공";
    }
}