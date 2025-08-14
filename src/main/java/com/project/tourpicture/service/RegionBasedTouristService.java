package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.dto.RegionBasedTouristDTO;
import com.project.tourpicture.repository.RegionBasedTouristRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegionBasedTouristService {
    private final RegionBasedTouristRepository regionBaseRepo;

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 지역 기반 관광지 DTO 반환 메서드
    @Transactional
    public List<RegionBasedTouristDTO> getRegionBasedTourists(String areaCd, String sigunguCd, int contentTypeId) {
        List<RegionBasedTourist> cachedData = regionBaseRepo.findByAreaCdAndSigunguCdAndContentTypeId(areaCd, sigunguCd, String.valueOf(contentTypeId));

        // 데이터 없으면 요청
        if (cachedData.isEmpty()) {
            cachedData = fetchAndSaveTouristData(areaCd, sigunguCd, contentTypeId);
        }

        // 1주일 이상된 경우 갱신
        LocalDateTime lastUpdated = cachedData.get(0).getUpdatedAt();
        if (lastUpdated.isBefore(LocalDateTime.now().minusDays(7))) {
            regionBaseRepo.deleteByAreaCdAndSigunguCdAndContentTypeId(areaCd, sigunguCd, String.valueOf(contentTypeId));
            cachedData = fetchAndSaveTouristData(areaCd, sigunguCd, contentTypeId);
        }

        return cachedData.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 지역 기반 관광지 엔티티 반환 메서드
    @Transactional
    public List<RegionBasedTourist> getRegionBasedTouristsEntity(String areaCd, String sigunguCd, int contentTypeId) {
        System.out.println(areaCd + " " + sigunguCd);
        List<RegionBasedTourist> cachedData =  regionBaseRepo.findByAreaCdAndSigunguCdAndContentTypeId(areaCd, sigunguCd, String.valueOf(contentTypeId));

        // 데이터 없으면 요청
        if (cachedData.isEmpty()) {
            cachedData = fetchAndSaveTouristData(areaCd, sigunguCd, contentTypeId);
            if (cachedData == null || cachedData.isEmpty()) {
                log.warn("관광지 데이터를 가져올 수 없습니다: areaCd={}, sigunguCd={}", areaCd, sigunguCd);
                return Collections.emptyList();
            }
        }

        // 1주일 이상된 경우 갱신
        LocalDateTime lastUpdated = cachedData.get(0).getUpdatedAt();
        if (lastUpdated == null || lastUpdated.isBefore(LocalDateTime.now().minusDays(7))) {
            regionBaseRepo.deleteByAreaCdAndSigunguCdAndContentTypeId(areaCd, sigunguCd, String.valueOf(contentTypeId));
            List<RegionBasedTourist> refreshedData = fetchAndSaveTouristData(areaCd, sigunguCd, contentTypeId);
            if (refreshedData != null && !refreshedData.isEmpty()) {
                return refreshedData;
            } else {
                return Collections.emptyList();
            }
        }

        return cachedData;
    }

    // 지역 기반 관광지 조회 및 저장 메서드
    public List<RegionBasedTourist> fetchAndSaveTouristData(String areaCd, String sigunguCd, int contentTypeId) {
        try {
            String url = "https://apis.data.go.kr/B551011/KorService2/areaBasedList2"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=300"
                    + "&MobileOS=" + "WEB"
                    + "&MobileApp=" + "One-cut-travel"
                    + "&contentTypeId=" + contentTypeId
                    + "&lDongRegnCd=" + areaCd
                    + "&lDongSignguCd=" + sigunguCd
                    + "&arrange=C"
                    + "&_type=json";

            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode header = root.path("response").path("header");
            log.info("지역기반 관광지 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            List<RegionBasedTourist> dataList = objectMapper.readerForListOf(RegionBasedTourist.class).readValue(itemArray);

            if (dataList != null && !dataList.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();
                List<RegionBasedTourist> filteredList = dataList.stream()
                        .filter(t -> t.getTitle() == null || !t.getTitle().contains("회사") || t.getFirstImage() == null)
                        .peek(t -> t.setUpdatedAt(now))
                        .collect(Collectors.toList());

                if (!filteredList.isEmpty()) {
                    regionBaseRepo.saveAll(filteredList);
                    log.info("{}{} 지역기반 관광지 저장 완료 (제외된 {}건)",
                            areaCd, sigunguCd, dataList.size() - filteredList.size());
                } else {
                    log.info("{}{} 지역기반 관광지 저장 대상 없음", areaCd, sigunguCd);
                }
            }

            return dataList;

        } catch (IOException e) {
            log.error("지역기반 관광지 조회 중 예외 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // Dto 변환
    private RegionBasedTouristDTO toDTO(RegionBasedTourist entity) {
        return new RegionBasedTouristDTO(
                entity.getAddr1(),
                entity.getAddr2(),
                entity.getContentId(),
                entity.getContentTypeId(),
                entity.getCreatedTime(),
                entity.getModifiedTime(),
                entity.getFirstImage(),
                entity.getFirstImage2(),
                entity.getCpyrhtDivCd(),
                entity.getMapX(),
                entity.getMapY(),
                entity.getMlevel(),
                entity.getTitle(),
                entity.getZipcode()
        );
    }
}
