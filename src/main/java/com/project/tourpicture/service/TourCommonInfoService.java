package com.project.tourpicture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.project.tourpicture.dto.TourCommonInfoDTO;
import com.project.tourpicture.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.project.tourpicture.util.AppUtils.getItemsNode;

/**
 * 모든 관광지 카테고리의 공통 정보 조회
 */
@Service
@RequiredArgsConstructor
public class TourCommonInfoService {

    private final RestTemplate restTemplate;

    @Value("${api.key}")
    private String serviceKey;

    public TourCommonInfoDTO getTourCommonInfo(String contentId) {

        URI uri = UriComponentsBuilder.fromUriString("https://apis.data.go.kr/B551011/KorService2/detailCommon2")
                .queryParam("serviceKey", serviceKey)
                .queryParam("MobileOS", "WEB")
                .queryParam("MobileApp", "hancuttrip")
                .queryParam("contentId", contentId)
                .queryParam("numOfRows", 1)
                .queryParam("pageNo", 1)
                .queryParam("_type", "json")
                .build(true)
                .toUri();

        ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
        try {
            return parseTourCommonInfoResponse(response.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("외부 API 응답 파싱 오류", e);
        } catch (IndexOutOfBoundsException e) {
            throw new NotFoundException("해당 콘텐츠 ID에 대한 정보 없음");
        }
    }

    private TourCommonInfoDTO parseTourCommonInfoResponse(String json) throws JsonProcessingException {
        JsonNode items = getItemsNode(json);
        List<TourCommonInfoDTO> result = new ArrayList<>();

        for (JsonNode item : items) {
            TourCommonInfoDTO dto = new TourCommonInfoDTO();

            dto.setContentId(item.get("contentid").asText());
            dto.setContentTypeId(item.get("contenttypeid").asText());
            dto.setName(item.path("title").textValue());

            // 홈페이지 링크 파싱
            String homepage = item.path("homepage").textValue();
            String decoded = homepage.replace("\\u003C", "<").replace("\\u003E", ">");
            Pattern pattern = Pattern.compile("href=\"(.*?)\"");
            Matcher matcher = pattern.matcher(decoded);
            String homepageUrl = matcher.find() ? matcher.group(1) : "";

            dto.setHomepage(homepageUrl);
            dto.setImageUrl(item.path("firstimage").asText());
            dto.setAddress1(item.path("addr1").asText());
            dto.setAddress2(item.path("addr2").asText());
            dto.setZipcode(item.path("zipcode").asText());
            dto.setMapX(item.path("mapx").asText());
            dto.setMapY(item.path("mapy").asText());
            dto.setOverview(item.path("overview").asText());

            result.add(dto);
        }
        return result.get(0);
    }
}
