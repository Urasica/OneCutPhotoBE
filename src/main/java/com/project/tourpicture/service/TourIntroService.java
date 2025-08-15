package com.project.tourpicture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.tourpicture.dto.intro.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.project.tourpicture.util.AppUtils.getItemsNode;

/**
 * 관광지 카테고리별 소개 정보 조회
 * 관광지(12), 문화시설(14)
 */
@Service
@RequiredArgsConstructor
public class TourIntroService {

    private final RestTemplate restTemplate;

    @Value("${api.key}")
    private String serviceKey;

    public Object getIntroByCategory(String contentId, String contentTypeId){

        URI uri = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/B551011/KorService2/detailIntro2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "TourApp")
                .queryParam("contentId", contentId)
                .queryParam("contentTypeId", contentTypeId)
                .queryParam("numOfRows", 1)
                .queryParam("pageNo", 1)
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return parseTourIntroResponse(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("외부 API 응답 파싱 오류", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("접근 권한 오류");
        }
    }

    private Object parseTourIntroResponse(String json) throws JsonProcessingException, IllegalAccessException {
        JsonNode items = getItemsNode(json);

        List<Object> result = new ArrayList<>();
        for (JsonNode item : items) {
            String contentTypeId = item.get("contenttypeid").asText();
            switch (contentTypeId) {
                case "12" -> { //관광지
                    TourIntroDTO dto = createTourIntroDTO(
                            item.path("chkpet").asText(),
                            item.path("expagerange").asText(),
                            item.path("infocenter").asText(),
                            item.path("opendate").asText(),
                            item.path("parking").asText(),
                            item.path("restdate").asText(),
                            item.path("useseason").asText(),
                            item.path("usetime").asText()
                    );
                    result.add(dto);
                }
                case "14" -> { //문화시설
                    CultureIntroDTO dto = createCultureIntroDTO(
                            item.path("chkpetculture").asText(),
                            item.path("infocenterculture").asText(),
                            item.path("parkingculture").asText(),
                            item.path("parkingfee").asText(),
                            item.path("restdateculture").asText(),
                            item.path("usefee").asText(),
                            item.path("usetimeculture").asText(),
                            item.path("spendtime").asText()
                    );
                    result.add(dto);
                }
            }
        }

        for (Object dto : result) {
            for (Field field : dto.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                String value = (String) field.get(dto);
                if(value != null){
                    field.set(dto, value.replace("<br>", " "));
                }
            }
        }
        return result;
    }

    // 관광지 DTO
    public TourIntroDTO createTourIntroDTO(String chkPet, String expAgeRange, String infoCenter,
                                           String openDate, String parking, String restDate,
                                           String useSeason, String useTime) {
        TourIntroDTO dto = new TourIntroDTO();
        dto.setChkPet(chkPet);
        dto.setExpAgeRange(expAgeRange);
        dto.setInfoCenter(infoCenter);
        dto.setOpenDate(openDate);
        dto.setParking(parking);
        dto.setRestDate(restDate);
        dto.setUseSeason(useSeason);
        dto.setUseTime(useTime);
        return dto;
    }

    // 문화시설 DTO
    public CultureIntroDTO createCultureIntroDTO(String chkPet, String infoCenter, String parking,
                                                 String parkingFee, String restDate, String useFee,
                                                 String useTime, String spendTime) {
        CultureIntroDTO dto = new CultureIntroDTO();
        dto.setChkPet(chkPet);
        dto.setInfoCenter(infoCenter);
        dto.setParking(parking);
        dto.setParkingFee(parkingFee);
        dto.setRestDate(restDate);
        dto.setUseFee(useFee);
        dto.setUseTime(useTime);
        dto.setSpendTime(spendTime);
        return dto;
    }
}
