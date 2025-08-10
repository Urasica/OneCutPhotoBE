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
 * 관광지(12), 문화시설(14), 행사(15), 여행코스(25), 레포츠(28), 쇼핑(38)
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
                case "15" -> { //행사
                    EventIntroDTO dto = createEventIntroDTO(
                            item.path("agelimit").asText(),
                            item.path("bookingplace").asText(),
                            item.path("eventenddate").asText(),
                            item.path("eventhomepage").asText(),
                            item.path("eventplace").asText(),
                            item.path("eventstartdate").asText(),
                            item.path("playtime").asText(),
                            item.path("spendtimefestival").asText(),
                            item.path("sponsor1").asText(),
                            item.path("sponsor1tel").asText(),
                            item.path("usetimefestival").asText()
                    );
                    result.add(dto);
                }
                case "25" -> { //여행코스
                    TourCourseIntroDTO dto = createTourCourseIntroDTO(
                            item.path("distance").asText(),
                            item.path("infocentertourcourse").asText(),
                            item.path("schedule").asText(),
                            item.path("taketime").asText(),
                            item.path("theme").asText()
                    );
                    result.add(dto);
                }
                case "28" -> { //레포츠
                    LeportsIntroDTO dto = createLeportsIntroDTO(
                            item.path("chkpetleports").asText(),
                            item.path("expagerangeleports").asText(),
                            item.path("infocenterleports").asText(),
                            item.path("openperiod").asText(),
                            item.path("parkingleports").asText(),
                            item.path("parkingfeeleports").asText(),
                            item.path("reservation").asText(),
                            item.path("restdateleports").asText(),
                            item.path("usefeeleports").asText(),
                            item.path("usetimeleports").asText()
                    );
                    result.add(dto);
                }
                case "38" -> { //쇼핑
                    ShoppingIntroDTO dto = createShoppingIntroDTO(
                            item.path("chkpetshopping").asText(),
                            item.path("infocentershopping").asText(),
                            item.path("opendateshopping").asText(),
                            item.path("opentime").asText(),
                            item.path("parkingshopping").asText(),
                            item.path("restdateshopping").asText(),
                            item.path("restroom").asText(),
                            item.path("saleitem").asText(),
                            item.path("scaleshopping").asText()
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

    // 행사/공연/축제 DTO
    public EventIntroDTO createEventIntroDTO(String ageLimit, String bookingPlace, String eventEndDate,
                                             String eventHomepage, String eventPlace, String eventStartDate,
                                             String playTime, String spendTime, String sponsor,
                                             String sponsorTel, String useFee) {
        EventIntroDTO dto = new EventIntroDTO();
        dto.setAgeLimit(ageLimit);
        dto.setBookingPlace(bookingPlace);
        dto.setEventEndDate(eventEndDate);
        dto.setEventHomepage(eventHomepage);
        dto.setEventPlace(eventPlace);
        dto.setEventStartDate(eventStartDate);
        dto.setPlayTime(playTime);
        dto.setSpendTime(spendTime);
        dto.setSponsor(sponsor);
        dto.setSponsorTel(sponsorTel);
        dto.setUseFee(useFee);
        return dto;
    }

    // 여행코스 DTO
    public TourCourseIntroDTO createTourCourseIntroDTO(String distance, String infoCenter, String schedule,
                                                       String takeTime, String theme) {
        TourCourseIntroDTO dto = new TourCourseIntroDTO();
        dto.setDistance(distance);
        dto.setInfoCenter(infoCenter);
        dto.setSchedule(schedule);
        dto.setTakeTime(takeTime);
        dto.setTheme(theme);
        return dto;
    }

    // 레포츠 DTO
    public LeportsIntroDTO createLeportsIntroDTO(String chkPet, String expAgeRange, String infoCenter,
                                                 String openPeriod, String parking, String parkingFee,
                                                 String reservation, String restDate, String useFee,
                                                 String useTime) {
        LeportsIntroDTO dto = new LeportsIntroDTO();
        dto.setChkPet(chkPet);
        dto.setExpAgeRange(expAgeRange);
        dto.setInfoCenter(infoCenter);
        dto.setOpenPeriod(openPeriod);
        dto.setParking(parking);
        dto.setParkingFee(parkingFee);
        dto.setReservation(reservation);
        dto.setRestDate(restDate);
        dto.setUseFee(useFee);
        dto.setUseTime(useTime);
        return dto;
    }

    // 쇼핑 DTO
    public ShoppingIntroDTO createShoppingIntroDTO(String chkPet, String infoCenter, String openDate,
                                                   String openTime, String parking, String restDate,
                                                   String restroom, String saleItem, String scaleShopping) {
        ShoppingIntroDTO dto = new ShoppingIntroDTO();
        dto.setChkPet(chkPet);
        dto.setInfoCenter(infoCenter);
        dto.setOpenDate(openDate);
        dto.setOpenTime(openTime);
        dto.setParking(parking);
        dto.setRestDate(restDate);
        dto.setRestroom(restroom);
        dto.setSaleItem(saleItem);
        dto.setScaleShopping(scaleShopping);
        return dto;
    }
}
