package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.AreaEntity;
import com.project.tourpicture.dao.AreaId;
import com.project.tourpicture.repository.AreaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api.key}")
    private String apiKey;
    String MobileOS = "WEB";
    String MobileApp = "One-cut-travel";

    public void fetchAndSaveAllAreas() {
        try {
            log.info("시도(region) 목록 API 호출 시작");
            String url = "http://apis.data.go.kr/B551011/KorService2/areaCode2"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=" + 1
                    + "&numOfRows=" + 20
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&_type=json";

            URI uri = URI.create(url);
            String regionJson = restTemplate.getForObject(uri, String.class);

            JsonNode regionItems = objectMapper.readTree(regionJson)
                    .path("response")
                    .path("body")
                    .path("items")
                    .path("item");

            for (JsonNode region : regionItems) {
                String areaCd = region.path("code").asText();
                String areaNm = region.path("name").asText();

                log.info("시군구(sigungu) 목록 API 호출 시작 - areaCd: {}, areaNm: {}", areaCd, areaNm);

                String sigunguurl = "http://apis.data.go.kr/B551011/KorService2/areaCode2"
                        + "?serviceKey=" + apiKey
                        + "&pageNo=" + 1
                        + "&numOfRows=" + 35
                        + "&MobileOS=" + MobileOS
                        + "&MobileApp=" + MobileApp
                        + "&areaCode=" + areaCd
                        + "&_type=json";

                URI sigunguuri = URI.create(sigunguurl);
                String sigunguJson = restTemplate.getForObject(sigunguuri, String.class);

                JsonNode sigunguItems = objectMapper.readTree(sigunguJson)
                        .path("response")
                        .path("body")
                        .path("items")
                        .path("item");

                List<AreaEntity> areaEntities = new ArrayList<>();
                for (JsonNode sigungu : sigunguItems) {
                    String sigunguCd = sigungu.path("code").asText();
                    String sigunguNm = sigungu.path("name").asText();

                    areaEntities.add(new AreaEntity(
                            new AreaId(areaCd, sigunguCd),
                            sigunguNm,
                            areaNm
                    ));
                }

                if(!areaEntities.isEmpty()) {
                    areaRepository.deleteByIdAreaCd(areaCd);
                    areaRepository.saveAll(areaEntities);
                }

                log.info("저장 완료 - areaCd: {}, 총 {}건", areaCd, areaEntities.size());
            }

        } catch (Exception e) {
            log.error("지역 데이터 저장 중 오류 발생", e);
        }
    }
}
