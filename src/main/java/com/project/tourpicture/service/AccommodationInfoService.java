package com.project.tourpicture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.tourpicture.dto.AccommodationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.project.tourpicture.util.AppUtils.*;

@Service
@RequiredArgsConstructor
public class AccommodationInfoService {

    private final RestTemplate restTemplate;

    @Value("${api.key}")
    private String serviceKey;

    // 해당 지역의 숙박 리스트 제공
    public List<AccommodationDTO> getAccommodations(String areaCode, String sigunguCode, int numOfRows) {

        URI uri = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/B551011/KorService2/searchStay2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("arrange", "C")
                .queryParam("lDongRegnCd", areaCode)
                .queryParam("lDongSignguCd", sigunguCode)
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return parseAccommodationInfoResponse(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("외부 API 응답 파싱 오류", e);
        }
    }

    private List<AccommodationDTO> parseAccommodationInfoResponse(String json) throws JsonProcessingException {
        JsonNode items = getItemsNode(json);
        List<AccommodationDTO> results = new ArrayList<>();

        for (JsonNode item : items) {
            AccommodationDTO dto = new AccommodationDTO();
            dto.setName(item.path("title").asText());
            dto.setAddress(item.path("addr1").asText());
            dto.setMapX(item.path("mapx").asText());
            dto.setMapY(item.path("mapy").asText());
            dto.setImageUrl(item.path("firstimage").asText());

            results.add(dto);
        }
        return results;
    }
}
