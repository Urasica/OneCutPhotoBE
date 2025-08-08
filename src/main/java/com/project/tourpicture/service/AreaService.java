package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.AreaCd;
import com.project.tourpicture.dao.SigunguCd;
import com.project.tourpicture.repository.AreaCdRepository;
import com.project.tourpicture.repository.SigunguCdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class AreaService {
    private final AreaCdRepository areaCdRepository;
    private final SigunguCdRepository sigunguCdRepository;

    @Value("${api.key}")
    private String apiKey;
    private final String MobileOS = "WEB";
    private final String MobileApp = "One-cut-travel";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public void fetchAreaCd() {
        try {
            String url = "https://apis.data.go.kr/B551011/KorService2/ldongCode2"
                    + "?serviceKey=" + apiKey
                    + "&pageNo=1"
                    + "&numOfRows=20"
                    + "&MobileOS=" + MobileOS
                    + "&MobileApp=" + MobileApp
                    + "&lDongListYn=N"
                    + "&_type=json";

            URI uri = URI.create(url);
            String response = restTemplate.getForObject(uri, String.class);

            JsonNode root = objectMapper.readTree(response);

            JsonNode header = root.path("response").path("header");
            log.info("법정동 시도코드 API 응답 헤더: resultCode={}, resultMsg={}",
                    header.path("resultCode").asText(),
                    header.path("resultMsg").asText());

            JsonNode itemArray = root.path("response").path("body").path("items").path("item");

            List<AreaCd> dataList =
                    objectMapper.readerForListOf(AreaCd.class)
                            .readValue(itemArray);

            if(dataList != null && !dataList.isEmpty()) {
                areaCdRepository.deleteAll();
                areaCdRepository.saveAll(dataList);
                log.info("법정동 시도코드 API 저장 완료");
            }

        } catch (IOException e) {
            log.error("법정동 시도코드 조회 중 예외 발생: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public void fetchSigunguCd() {
        List<AreaCd> areaCdList = areaCdRepository.findAll();
        List<SigunguCd> sigunguCdList = new ArrayList<>();
        Set<String> existingCodes = new HashSet<>();

        for (AreaCd areaCd : areaCdList) {
            try {
                String url = "https://apis.data.go.kr/B551011/KorService2/ldongCode2"
                        + "?serviceKey=" + apiKey
                        + "&pageNo=1"
                        + "&numOfRows=30"
                        + "&MobileOS=" + MobileOS
                        + "&MobileApp=" + MobileApp
                        + "&lDongRegnCd=" + areaCd.getAreaCd()
                        + "&lDongListYn=N"
                        + "&_type=json";

                URI uri = URI.create(url);
                String response = restTemplate.getForObject(uri, String.class);
                JsonNode itemArray = objectMapper.readTree(response)
                        .path("response").path("body").path("items").path("item");

                List<SigunguCd> dataList =
                        objectMapper.readerForListOf(SigunguCd.class).readValue(itemArray);

                for (SigunguCd sigungu : dataList) {
                    String totalCode = areaCd.getAreaCd() + sigungu.getSigunguCd();
                    if (existingCodes.contains(totalCode)) {
                        log.warn("중복 totalCd 발견: {}", totalCode);
                        continue;
                    }
                    sigungu.setTotalCd(totalCode);
                    sigunguCdList.add(sigungu);
                    existingCodes.add(totalCode);
                }

            } catch (IOException e) {
                log.error("법정동 시군구코드 조회 실패: {}", e.getMessage(), e);
            }
        }

        if (!sigunguCdList.isEmpty()) {
            sigunguCdRepository.deleteAllInBatch(); // 핵심 수정 포인트
            sigunguCdRepository.saveAll(sigunguCdList);
            log.info("법정동 시군구코드 저장 완료 (총 {}건)", sigunguCdList.size());
        }
    }


    public List<AreaCodeDto> getAreaCodes() {
        return areaCdRepository.findAll().stream()
                .map(area -> new AreaCodeDto(area.getAreaCd(), area.getAreaNm()))
                .toList();
    }

    public List<SigunguCodeDto> getSignguCodesWithNameByAreaCode(String areaCdPrefix) {
        return sigunguCdRepository.findAll().stream()
                .filter(sigungu -> sigungu.getTotalCd().startsWith(areaCdPrefix))
                .map(sigungu -> new SigunguCodeDto(sigungu.getSigunguCd(), sigungu.getSigunguNm()))
                .toList();
    }

    public record AreaCodeDto(String areaCd, String areaNm) {}
    public record SigunguCodeDto(String sigunguCd, String sigunguNm) {}
}