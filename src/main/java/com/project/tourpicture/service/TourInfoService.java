package com.project.tourpicture.service;

import com.project.tourpicture.dto.RelatedTourRequestDTO;
import com.project.tourpicture.dto.RelatedTourResponseDTO;
import com.project.tourpicture.dto.TourPhotoDTO;
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
            RelatedTourRequestDTO dto) {

        URI uri = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/TarRlteTarService1/searchKeyword1")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", dto.getNumOfRows())
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("baseYm", dto.getBaseYm())
                .queryParam("areaCd", dto.getAreaCode())
                .queryParam("signguCd", dto.getSigunguCode())
                .queryParam("keyword", UriUtils.encodeQueryParam(dto.getKeyword(), "UTF-8"))
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return parseRelatedTourResponse(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("해당 관광지의 연관관광지 조회 실패", e);
        }
    }

    // 연관관광지 응답값 파싱
    private List<RelatedTourResponseDTO> parseRelatedTourResponse(String json) throws JsonProcessingException {
        JsonNode items = getItemsNode(json);
        List<RelatedTourResponseDTO> results = new ArrayList<>();

        for (JsonNode item : items) {
            RelatedTourResponseDTO dto = new RelatedTourResponseDTO();
            String rlteTatsNm = item.path("rlteTatsNm").textValue();
            dto.setRelatedTourName(rlteTatsNm);
            dto.setRelatedTourAreaName(item.path("rlteRegnNm").textValue());
            dto.setRelatedTourAreaCode(item.path("rlteRegnCd").textValue());
            dto.setRelatedTourSigunguName(item.path("rlteSignguNm").textValue());
            dto.setRelatedTourSigunguCode(item.path("rlteSignguCd").textValue());
            dto.setRelatedTourCategoryLarge(item.path("rlteCtgryLclsNm").textValue());
            dto.setRelatedTourCategorySmall(item.path("rlteCtgrySclsNm").textValue());
            UriUtils.encodeQueryParam(item.path("rlteTatsNm").textValue(), "UTF-8");
            dto.setPhoto(getTourPhotos(1, UriUtils.encodeQueryParam(rlteTatsNm, "UTF-8")));

            results.add(dto);
        }

        return results;
    }

    // 관광지별 사진 조회
    public List<TourPhotoDTO> getTourPhotos(int numOfRows, String keyword) {
        String word = spacingService.spacingWord(keyword);

        URI uri = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/PhotoGalleryService1/gallerySearchList1")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("arrange", "B")
                .queryParam("keyword", UriUtils.encodeQueryParam(word, "UTF-8"))
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        // 1차 요청
        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        List<TourPhotoDTO> results;
        try {
            results = parseTourPhotoResponse(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("해당 관광지의 사진 조회 실패", e);
        }

        // 결과가 비어있으면 재요청
        if (results.isEmpty()) {
            System.out.println("[재요청] 관광지 사진이 비어있음. 0.5초 대기 후 재요청합니다.");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            response = restTemplate.getForEntity(uri, String.class);
            try {
                results = parseTourPhotoResponse(response.getBody());
            } catch (JsonProcessingException e) {
                throw new RuntimeException("재요청 시에도 관광지 사진 조회 실패", e);
            }

            if (results.isEmpty()) {
                System.out.println("[재요청 실패] 재요청했지만 여전히 결과가 없습니다.");
            } else {
                System.out.println("[재요청 성공] 결과가 있습니다. size = " + results.size());
            }
        } else {
            System.out.println("[정상 응답] size = " + results.size());
        }

        return results;
    }

    // 관광지사진 응답값 파싱
    private List<TourPhotoDTO> parseTourPhotoResponse(String json) throws JsonProcessingException {
        JsonNode items = getItemsNode(json);

        List<TourPhotoDTO> results = new ArrayList<>();

        for (JsonNode item : items) {
            TourPhotoDTO dto = new TourPhotoDTO();
            dto.setImageUrl(item.path("galWebImageUrl").textValue());
            dto.setTakenMonth(item.path("galPhotographyMonth").textValue());
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