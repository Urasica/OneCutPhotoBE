package com.project.tourpicture.service;

import com.project.tourpicture.dao.RelatedTourPhoto;
import com.project.tourpicture.dto.RelatedTourResponseDTO;
import com.project.tourpicture.dto.TourPhotoDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.repository.RelatedTourPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
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
    private final RelatedTourPhotoRepository relatedTourPhotoRepository;

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
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "외부 API 응답 파싱 오류");
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

    // 관광지별 대표사진 조회
    @Transactional
    public TourPhotoDTO getTourPhoto(String keyword) {
        RelatedTourPhoto photo = relatedTourPhotoRepository.findByOriginal(keyword.trim())
                .orElseGet(() -> savePhoto(keyword.trim()));

        TourPhotoDTO dto = new TourPhotoDTO();
        dto.setImageUrl(photo.getImageUrl());
        dto.setTakenMonth(photo.getTakenMonth());
        return dto;
    }

    // 관광지별 대표사진 저장
    private RelatedTourPhoto savePhoto(String keyword) {
        String trimmed = keyword.trim();
        String spaced = spacingService.spacingWord(trimmed.contains("/") ? trimmed.split("/")[0] : trimmed);
        RelatedTourPhoto relatedTourPhoto = new RelatedTourPhoto();
        relatedTourPhoto.setOriginal(keyword.trim());
        relatedTourPhoto.setSpaced(spaced);

        List<TourPhotoDTO> photos = requestTourPhotos(1, spaced);
        if (photos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "관광지 사진을 찾을 수 없음");
        }

        TourPhotoDTO dto = photos.get(0);
        relatedTourPhoto.setImageUrl(dto.getImageUrl());
        relatedTourPhoto.setTakenMonth(dto.getTakenMonth());
        relatedTourPhotoRepository.save(relatedTourPhoto);
        return relatedTourPhoto;
    }

    // 관광지별 사진 요청
    private List<TourPhotoDTO> requestTourPhotos(int numOfRows, String word) {
        URI uri = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/PhotoGalleryService1/gallerySearchList1")
                .queryParam("serviceKey", serviceKey)
                .queryParam("numOfRows", numOfRows)
                .queryParam("pageNo", 1)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("arrange", "C")
                .queryParam("keyword", UriUtils.encodeQueryParam(word.trim(), "UTF-8"))
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);

        try {
            return parseTourPhotoResponse(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("해당 관광지의 사진 조회 실패", e);
        }
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