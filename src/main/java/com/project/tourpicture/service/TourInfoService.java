package com.project.tourpicture.service;


import com.project.tourpicture.dto.RelatedTourResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

@Service
@RequiredArgsConstructor
public class TourInfoService {

    private final RestTemplate restTemplate;
    private final SpacingService spacingService;

    @Value("${api.key}")
    private String serviceKey;

    // 관광지별(키워드) 연관관광지 조회
    public List<RelatedTourResponseDTO> getRelatedTours(
            int numOfRow, String baseYm, String areaCode, String sigunguCode, String keyword) {

        URI uri = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/TarRlteTarService1/searchKeyword1")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRow)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("baseYm", baseYm)
                .queryParam("areaCd", areaCode)
                .queryParam("signguCd", sigunguCode)
                .queryParam("keyword", UriUtils.encodeQueryParam(keyword, "UTF-8"))
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return parseRelatedTourResponse(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("외부 API 응답 파싱 오류", e);
        }
    }

    // 연관관광지 응답값 파싱
    private List<RelatedTourResponseDTO> parseRelatedTourResponse(String json) throws JsonProcessingException {
        JsonNode items = getItemsNode(json);
        List<RelatedTourResponseDTO> results = new ArrayList<>();

        for (JsonNode item : items) {
            RelatedTourResponseDTO dto = new RelatedTourResponseDTO();
            dto.setRelatedTourName(item.path("rlteTatsNm").textValue());
            dto.setRelatedTourAreaName(item.path("rlteRegnNm").textValue());
            dto.setRelatedTourAreaCode(item.path("rlteRegnCd").textValue());
            dto.setRelatedTourSigunguName(item.path("rlteSignguNm").textValue());
            dto.setRelatedTourSigunguCode(item.path("rlteSignguCd").textValue());
            dto.setRelatedTourCategoryLarge(item.path("rlteCtgryLclsNm").textValue());
            dto.setRelatedTourCategorySmall(item.path("rlteCtgrySclsNm").textValue());

            results.add(dto);
        }

        return results;
    }

    // JsonNode 아이템 조회
    private static JsonNode getItemsNode(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        return root.path("response").path("body").path("items").path("item");
    }
}