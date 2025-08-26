package com.project.tourpicture.service;

import com.project.tourpicture.dao.HubTourismEntity;
import com.project.tourpicture.dao.KeywordBasedTourist;
import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.dto.TourCourseItemDTO;
import com.project.tourpicture.exception.NotFoundException;
import com.project.tourpicture.repository.KeywordBasedTouristRepository;
import com.project.tourpicture.repository.RegionBasedTouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.project.tourpicture.util.AppUtils.getDistance;
import static com.project.tourpicture.util.AppUtils.getLocation;

@Service
@RequiredArgsConstructor
public class TourCourseRecommendationService {

    private final RegionBasedTouristService regionBasedTouristService;
    private final RegionBasedTouristRepository regionBasedTouristRepository;
    private final KeywordBasedTouristRepository keywordBasedTouristRepository;
    private final HubTourismService hubTourismService;

    // 추천 코스 조회(거리 기준)
    public List<TourCourseItemDTO> getCourseByDistance(String contentId, String areaCode, String sigunguCode, int numOfCourse) {
        String startSpot;
        String startSpotMapX;
        String startSpotMapY;

        RegionBasedTourist startSpotInfo = getTourInfo(contentId, areaCode, sigunguCode);
        if (startSpotInfo != null) {
            startSpot = startSpotInfo.getTitle();
            startSpotMapX = startSpotInfo.getMapX();
            startSpotMapY = startSpotInfo.getMapY();
        } else {
            KeywordBasedTourist keywordBasedTourist = keywordBasedTouristRepository.findByContentId(contentId)
                    .orElseThrow(() -> new NotFoundException("해당 관광지의 위치 정보를 찾을 수 없어 코스 추천 불가"));
            startSpot = keywordBasedTourist.getTitle();
            startSpotMapX = keywordBasedTourist.getMapX();
            startSpotMapY = keywordBasedTourist.getMapY();
        }

        List<HubTourismEntity> hubTourismList = hubTourismService.getHubTourismWithRanking(areaCode, sigunguCode);// 관광지 리스트
        return buildCourseByDistance(startSpot, startSpotMapX, startSpotMapY, hubTourismList , numOfCourse);
    }

    // 거리 기반 코스 생성
    private List<TourCourseItemDTO> buildCourseByDistance(String startSpot, String mapX, String mapY,
                                                          List<HubTourismEntity> spots, int numOfCourse) {
        // 코스 포함 여부 체크용
        Set<String> visited = new HashSet<>();
        visited.add(startSpot);

        // 추천 코스
        List<TourCourseItemDTO> course = new ArrayList<>();
        TourCourseItemDTO startSpotDTO = createTourCourseDTO(startSpot, mapX, mapY);
        course.add(startSpotDTO);

        double[] location = new double[]{
                Double.parseDouble(mapX),
                Double.parseDouble(mapY)
        };

        for (int i = 0; i < numOfCourse; i++) {
            double finalCurrentX = location[0];
            double finalCurrentY = location[1];
            Optional<HubTourismEntity> nextSpotOptional = spots.stream()
                    .filter(s -> !visited.contains(s.getMatchedTourist().getTitle()))
                    .min(Comparator.comparingDouble(s ->
                            getDistance(finalCurrentY, finalCurrentX,
                                    Double.parseDouble(s.getMapX()), Double.parseDouble(s.getMapX()))
                    ));
            HubTourismEntity nextInfo = nextSpotOptional.orElseThrow(() -> new RuntimeException("다음 관광지가 없습니다."));

            String nextSpotName = nextInfo.getMatchedTourist().getTitle();
            visited.add(nextSpotName);
            TourCourseItemDTO nextSpotDTO = createTourCourseDTO(nextSpotName, nextInfo.getMapX(), nextInfo.getMapY());
            course.add(nextSpotDTO);

            location = getLocation(nextInfo);
        }
        return course;
    }

    // 해당 지역의 관광지 목록 조회
    private void getRegionTouristSpots(String areaCode, String sigunguCode) {
        try {
            List<RegionBasedTourist> regionBasedTourists = regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode, 12);
            regionBasedTourists.addAll(regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode, 14));
        } catch (RuntimeException e) {
            throw new NotFoundException("지역코드 혹은 시군구코드 입력 오류");
        }
    }

    // 관광지 정보 조회
    private RegionBasedTourist getTourInfo(String contentId, String areaCode, String sigunguCode) {
        getRegionTouristSpots(areaCode, sigunguCode);
        return regionBasedTouristRepository.findByContentId(contentId);
    }

    private TourCourseItemDTO createTourCourseDTO(String tourName, String mapX, String mapY) {
        TourCourseItemDTO tourCourseDTO = new TourCourseItemDTO();
        tourCourseDTO.setTourName(tourName);
        tourCourseDTO.setMapX(mapX);
        tourCourseDTO.setMapY(mapY);
        return tourCourseDTO;
    }
}
