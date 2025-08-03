package com.project.tourpicture.service;

import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.dao.TourismFocusInfo;
import com.project.tourpicture.dto.TourCourseDTO;
import com.project.tourpicture.exception.NotFoundException;
import com.project.tourpicture.repository.RegionBasedTouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TourCourseRecommendationService {

    private final RegionBasedTouristService regionBasedTouristService;
    private final RegionBasedTouristRepository regionBasedTouristRepository;
    private final TourismFocusInfoService tourismFocusInfoService;

    // 추천 코스 조회(거리 기준)
    public List<TourCourseDTO> getCourseByDistance(String startSpot, String areaCode, String sigunguCode, int numOfCourse) {
        RegionBasedTourist startSpotInfo = getTourInfo(startSpot, areaCode, sigunguCode);
        List<RegionBasedTourist> spotInfos = getRegionTouristSpots(areaCode, sigunguCode); //해당 지역의 모든 관광지

        return buildCourseByDistance(startSpot, startSpotInfo, spotInfos, numOfCourse);
    }

    // 거리 기반 코스 생성
    private List<TourCourseDTO> buildCourseByDistance(String startSpot, RegionBasedTourist startSpotInfo,
                                                      List<RegionBasedTourist> spots, int numOfCourse) {
        // 코스 포함 여부 체크용
        Set<String> visited = new HashSet<>();
        visited.add(startSpot);

        // 추천 코스
        List<TourCourseDTO> course = new ArrayList<>();
        TourCourseDTO startSpotDTO = createTourCourseDTO(startSpot, startSpotInfo.getMapX(), startSpotInfo.getMapY());
        course.add(startSpotDTO);

        double[] location = getLocation(startSpotInfo);

        for (int i = 0; i < numOfCourse; i++) {
            double finalCurrentX = location[0];
            double finalCurrentY = location[1];
            Optional<RegionBasedTourist> nextSpotOptional = spots.stream()
                    .filter(s -> !visited.contains(s.getTitle()))
                    .min(Comparator.comparingDouble(s ->
                            getDistance(finalCurrentY, finalCurrentX,
                                    Double.parseDouble(s.getMapY()), Double.parseDouble(s.getMapX()))
                    ));
            RegionBasedTourist nextInfo = nextSpotOptional.orElseThrow(() -> new RuntimeException("다음 관광지가 없습니다."));

            visited.add(nextInfo.getTitle());
            TourCourseDTO nextSpotDTO = createTourCourseDTO(nextInfo.getTitle(), nextInfo.getMapX(), nextInfo.getMapY());
            course.add(nextSpotDTO);

            location = getLocation(nextInfo);
        }
        return course;
    }

    // 해당 지역의 중심관광지 목록 조회
    private List<RegionBasedTourist> getRegionTouristSpots(String areaCode, String sigunguCode) {
        try {
            return regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode);
        } catch (RuntimeException e) {
            throw new NotFoundException("지역코드 혹은 시군구코드 입력 오류");
        }
    }

    // 관광지 정보 조회
    private RegionBasedTourist getTourInfo(String spot, String areaCode, String sigunguCode) {
        getRegionTouristSpots(areaCode, sigunguCode);
        return regionBasedTouristRepository.findByTitle(spot)
                .orElseThrow(() -> new NotFoundException("해당 관광지의 위치 정보를 찾을 수 없어 코스 추천 불가"));
    }

    // 경도, 위도 조회
    private double[] getLocation(RegionBasedTourist info) {
        return new double[]{
                Double.parseDouble(info.getMapX()), //경도
                Double.parseDouble(info.getMapY())  //위도
        };
    }

    // 관광지간 거리 계산
    private double getDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public TourCourseDTO createTourCourseDTO(String tourName, String mapX, String mapY) {
        TourCourseDTO tourCourseDTO = new TourCourseDTO();
        tourCourseDTO.setTourName(tourName);
        tourCourseDTO.setMapX(mapX);
        tourCourseDTO.setMapY(mapY);
        return tourCourseDTO;
    }
}
