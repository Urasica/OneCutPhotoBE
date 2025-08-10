package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.KeywordBasedTourist;
import com.project.tourpicture.dao.LclsSystemCd;
import com.project.tourpicture.dto.KeywordBasedTouristDTO;
import com.project.tourpicture.repository.KeywordBasedTouristRepository;
import com.project.tourpicture.repository.LclsSystemCdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeywordBasedTouristService {
    private final KeywordBasedTouristRepository keywordBaseRepo;
    private final LclsSystemCdRepository lclsSystemCdRepo;

    @Value("${api.key}")
    private String apiKey;
    private final String MobileOS = "WEB";
    private final String MobileApp = "One-cut-travel";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final Map<String,String> cache = new ConcurrentHashMap<>();

    // 키워드 기반 관광지 DTO 반환 메서드
    public List<KeywordBasedTouristDTO> getKeywordBasedTourists(String keyword) {
        List<KeywordBasedTourist> cachedData = keywordBaseRepo.findByKeyword(keyword);

        // 데이터 없으면 요청
        if (cachedData == null || cachedData.isEmpty()) {
            cachedData = fetchAndSaveTouristData(keyword);
            if (cachedData == null || cachedData.isEmpty()) {
                return Collections.emptyList();
            }
        }

        // 1주일 이상된 경우 갱신
        LocalDateTime lastUpdated = cachedData.get(0).getUpdatedAt();
        if (lastUpdated.isBefore(LocalDateTime.now().minusDays(7))) {
            keywordBaseRepo.deleteByKeyword(keyword);
            cachedData = fetchAndSaveTouristData(keyword);
        }

        return cachedData.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // 키워드기반 관광지 조회 및 저장 메서드
    private List<KeywordBasedTourist> fetchAndSaveTouristData(String keyword) {
        try {


            String url = "http://apis.data.go.kr/B551011/KorService2/searchKeyword2"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=100"
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)
                    + "&arrange=C"
                    + "&_type=json";

            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode header = root.path("response").path("header");
            log.info("키워드기반 관광지 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            List<KeywordBasedTourist> dataList =
                    objectMapper.readerForListOf(KeywordBasedTourist.class).readValue(itemArray);

            if (dataList != null && !dataList.isEmpty()) {
                LocalDateTime now = LocalDateTime.now();

                List<KeywordBasedTourist> filteredList = dataList.stream()
                        .filter(t -> t.getTitle() == null || !t.getTitle().contains("회사"))
                        .peek(t -> {
                            t.setUpdatedAt(now);
                            t.setKeyword(keyword);
                        })
                        .collect(Collectors.toList());

                if (!filteredList.isEmpty()) {
                    keywordBaseRepo.saveAll(filteredList);
                    log.info("키워드='{}' 키워드기반 관광지 저장 완료 (제외된 {}건)",
                            keyword, dataList.size() - filteredList.size());
                } else {
                    log.info("키워드='{}' 키워드기반 관광지 저장 대상 없음", keyword);
                }
            }

            return dataList;

        } catch (IOException e) {
            log.error("키워드기반 관광지 조회 중 예외 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    private String resolveCodeName(String code) {
        if (code == null) return null;
        return cache.computeIfAbsent(code, k ->
                lclsSystemCdRepo.findById(k).map(LclsSystemCd::getName).orElse(k)
        );
    }

    // Dto 변환
    private KeywordBasedTouristDTO toDTO(KeywordBasedTourist entity) {
        String codeName = resolveCodeName(entity.getLclsSystemCd());

        return new KeywordBasedTouristDTO(
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
                entity.getZipcode(),
                codeName
        );
    }
}
