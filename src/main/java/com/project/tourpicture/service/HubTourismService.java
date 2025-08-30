package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.HubTourism;
import com.project.tourpicture.dao.HubTourismEntity;
import com.project.tourpicture.dto.RegionBasedTouristDTO;
import com.project.tourpicture.repository.HubTourismEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class HubTourismService {
    private final HubTourismEntityRepository entityRepository;
    private final RegionBasedTouristService regionBasedTouristService;

    @Value("${api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Transactional
    public List<HubTourismEntity> getHubTourismWithRanking(String areaCd, String sigunguCd) {
        String targetYm = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .minusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyyMM"));

        String totalSigunguCd = areaCd + sigunguCd;

        // HubTourismEntity 조회
        List<HubTourismEntity> hubList = entityRepository
                .findByAreaCdAndSigunguCdAndBaseYm(areaCd, totalSigunguCd, targetYm);

        // 최신 데이터가 없으면 fetchHubTourism 호출
        if (hubList.isEmpty()) {
            fetchHubTourism(areaCd, totalSigunguCd);
            hubList = entityRepository.findByAreaCdAndSigunguCdAndBaseYm(areaCd, totalSigunguCd, targetYm);
        }

        if (hubList.isEmpty()) {
            log.warn("HubTourismEntity 조회 후에도 데이터 없음: areaCd={}, sigunguCd={}", areaCd, totalSigunguCd);
            return Collections.emptyList();
        }

        boolean anyMatched = hubList.stream().anyMatch(h -> h.getMatchedContentId() != null);

        if (!anyMatched) {
            // 이번 달 첫 매칭 → 후보 조회 후 safeMatch 수행
            List<RegionBasedTouristDTO> candidates = regionBasedTouristService.getRegionBasedTourists(areaCd, sigunguCd, 12);
            candidates.addAll(regionBasedTouristService.getRegionBasedTourists(areaCd, sigunguCd, 14));

            for (HubTourismEntity hub : hubList) {
                RegionBasedTouristDTO matched = safeMatch(hub, candidates);
                if (matched != null) {
                    hub.setMatchedContentId(matched.getContentId());
                    entityRepository.save(hub);
                }
            }
        }

        // 반환 시 matchedTourist DTO 주입
        Map<String, RegionBasedTouristDTO> dtoMap = Stream.concat(
                regionBasedTouristService.getRegionBasedTourists(areaCd, sigunguCd, 12).stream(),
                regionBasedTouristService.getRegionBasedTourists(areaCd, sigunguCd, 14).stream()
        ).collect(Collectors.toMap(RegionBasedTouristDTO::getContentId, dto -> dto, (a, b) -> a));

        for (HubTourismEntity hub : hubList) {
            if (hub.getMatchedContentId() != null) {
                hub.setMatchedTourist(dtoMap.get(hub.getMatchedContentId()));
            }
        }

        // 매칭된 항목만 필터 후, 순위 기준 정렬 후 반환
        return hubList.stream()
                .filter(h -> h.getMatchedTourist() != null) // 매칭된 것만
                .sorted(Comparator.comparingInt(h -> h.getHubRank() != null ? h.getHubRank() : Integer.MAX_VALUE))
                .toList();
    }


    private RegionBasedTouristDTO safeMatch(HubTourismEntity hub, List<RegionBasedTouristDTO> candidates) {
        String baseX = hub.getMapX();
        String baseY = hub.getMapY();

        if (baseX == null || baseY == null) return null;

        String hubName = normalize(hub.getHubTatsNm());

        List<RegionBasedTouristDTO> matched = candidates.stream()
                .filter(t -> {
                    // 좌표 거리 계산
                    double dist = distanceInMeters(baseY, baseX, t.getMapY(), t.getMapX());
                    if (dist > 500) return false;

                    // 이름 정규화 후 완전 일치 또는 포함만 허용
                    String touristName = normalize(t.getTitle());
                    return hubName.equals(touristName) || touristName.contains(hubName);
                })
                .toList();

        return matched.size() == 1 ? matched.get(0) : null;
    }

    private String normalize(String name) {
        if (name == null) return "";
        return name
                .toLowerCase()
                .replaceAll("\\s+", "")     // 모든 공백 제거
                .replaceAll("[/&-]", "")    // / & - 같은 기호 제거
                .replaceAll("\\(.*?\\)", "")// 괄호 안 내용 제거
                .trim();
    }

    // 하버사인 공식
    private double distanceInMeters(String lat1Str, String lon1Str, String lat2Str, String lon2Str) {
        if (lat1Str == null || lon1Str == null || lat2Str == null || lon2Str == null) return Double.MAX_VALUE;
        double lat1 = Double.parseDouble(lat1Str);
        double lon1 = Double.parseDouble(lon1Str);
        double lat2 = Double.parseDouble(lat2Str);
        double lon2 = Double.parseDouble(lon2Str);

        final int R = 6371000; // 지구 반경 (m)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    private void fetchHubTourism(String areaCd, String sigunguCd) {
        try {
            String day = LocalDate.now(ZoneId.of("Asia/Seoul"))
                    .minusMonths(2)
                    .format(DateTimeFormatter.ofPattern("yyyyMM"));

            String url = "https://apis.data.go.kr/B551011/LocgoHubTarService1/areaBasedList1"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=300"
                    + "&MobileOS=" + "WEB"
                    + "&MobileApp=" + "One-cut-travel"
                    + "&baseYm=" + day
                    + "&areaCd=" + areaCd
                    + "&signguCd=" + sigunguCd
                    + "&_type=json";

            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);
            JsonNode header = root.path("response").path("header");
            log.info("중심 관광지 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            List<HubTourism> dataList = objectMapper.readerForListOf(HubTourism.class).readValue(itemArray);

            if (dataList != null && !dataList.isEmpty()) {
                List<HubTourismEntity> entities = dataList.stream()
                        .map(dto -> {
                            HubTourismEntity entity = new HubTourismEntity();
                            entity.setHubTatsCd(dto.getHubTatsCd());
                            entity.setBaseYm(dto.getBaseYm());
                            entity.setAreaCd(dto.getAreaCd());
                            entity.setSigunguCd(dto.getSigunguCd());
                            entity.setHubTatsNm(dto.getHubTatsNm());
                            entity.setMapX(dto.getMapX());
                            entity.setMapY(dto.getMapY());

                            try {
                                entity.setHubRank(Integer.valueOf(dto.getHubRank()));
                            } catch (NumberFormatException e) {
                                log.warn("hubRank 변환 실패: {}", dto.getHubRank());
                                entity.setHubRank(null);
                            }

                            // matchedTourist 는 아직 매칭 안하는 단계임
                            entity.setMatchedTourist(null);

                            return entity;
                        })
                        .toList();

                entityRepository.saveAll(entities);
            }

        } catch (IOException e) {
            log.error("중심 관광지 조회 중 예외 발생: {}", e.getMessage(), e);
        }
    }
}
