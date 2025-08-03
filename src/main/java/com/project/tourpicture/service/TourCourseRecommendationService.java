package com.project.tourpicture.service;

import com.project.tourpicture.dao.CentralTouristInfo;
import com.project.tourpicture.dao.TourismFocusInfo;
import com.project.tourpicture.dto.TourCourseDTO;
import com.project.tourpicture.exception.NotFoundException;
import com.project.tourpicture.repository.CentralTouristInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TourCourseRecommendationService {

    private final CentralTouristService centralTouristService;
    private final CentralTouristInfoRepository centralTouristInfoRepository;
    private final TourismFocusInfoService tourismFocusInfoService;
    private static final String EXCLUDED_CATEGORY = "숙박";

    // 추천 코스 조회(해당 지역의 방문률 기준)
    public List<TourCourseDTO> getCourseByPopularity(String startSpot, String areaCode, String sigunguCode, int numOfCourse) {
        List<CentralTouristInfo> centralTouristList = getCentralTouristSpots(areaCode, sigunguCode);
        CentralTouristInfo startInfo = getTourInfo(startSpot, areaCode, sigunguCode);

        // 평균 예측 방문률
        Map<String, Double> spotVisitRate = new HashMap<>();
        centralTouristList.parallelStream()
                .filter(info -> !info.getHubCtgryMclsNm().contains(EXCLUDED_CATEGORY))
                .filter(info -> !info.getHubTatsNm().equals(startSpot))
                .forEach(info -> {
                    List<TourismFocusInfo> focusList = tourismFocusInfoService.getTourismFocusByName(
                            "1", "7", areaCode, sigunguCode, info.getHubTatsNm());
                    if (focusList == null || focusList.isEmpty()) return;
                    double avgVisitRate = focusList.stream()
                            .mapToDouble(TourismFocusInfo::getCnctrRate)
                            .average()
                            .orElse(0.0);
                    synchronized (spotVisitRate) {
                        spotVisitRate.put(info.getHubTatsNm(), avgVisitRate);
                    }
                });

        // 코스 추천
        List<TourCourseDTO> course = new ArrayList<>();
        TourCourseDTO startSpotDTO = createTourCourseDTO(startSpot, startInfo.getMapX(), startInfo.getMapY());
        course.add(startSpotDTO);

        List<String> recommendedSpots = spotVisitRate.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(numOfCourse)
                .map(Map.Entry::getKey)
                .toList();

        for (String spot : recommendedSpots) {
            CentralTouristInfo info = getTourInfo(spot, areaCode, sigunguCode);
            TourCourseDTO dto = createTourCourseDTO(spot, info.getMapX(), info.getMapY());
            course.add(dto);
        }
        return course;
    }

    // 추천 코스 조회(거리 기준)
    public List<TourCourseDTO> getCourseByDistance(String startSpot, String areaCode, String sigunguCode, int numOfCourse) {
        CentralTouristInfo startSpotInfo = getTourInfo(startSpot, areaCode, sigunguCode);
        List<CentralTouristInfo> spotInfos = getCentralTouristSpots(areaCode, sigunguCode); //해당 지역의 모든 관광지

        return buildCourseByDistance(startSpot, startSpotInfo, spotInfos, numOfCourse);
    }

    // 추천 코스 조회(방문률 + 거리)
    public List<TourCourseDTO> getCourseByDistanceAndPopularity(String startSpot, String areaCode, String sigunguCode, int numOfCourse) {
        List<TourCourseDTO> popularSpots = getCourseByPopularity(startSpot, areaCode, sigunguCode, 30);
        popularSpots.removeIf(spot -> spot.getTourName().equals(startSpot));

        CentralTouristInfo startInfo = getTourInfo(startSpot, areaCode, sigunguCode);

        List<CentralTouristInfo> spotInfos = popularSpots.stream() //방문률 가장 높은 관광지 30개
                .map(s -> getTourInfo(s.getTourName(), areaCode, sigunguCode))
                .toList();

        return buildCourseByDistance(startSpot, startInfo, spotInfos, numOfCourse);
    }

    // 거리 기반 코스 생성
    private List<TourCourseDTO> buildCourseByDistance(String startSpot, CentralTouristInfo startSpotInfo,
                                                      List<CentralTouristInfo> spots, int numOfCourse) {
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
            Optional<CentralTouristInfo> nextSpotOptional = spots.stream()
                    .filter(s -> !visited.contains(s.getHubTatsNm()))
                    .filter(s -> !s.getHubCtgryMclsNm().contains(EXCLUDED_CATEGORY))
                    .min(Comparator.comparingDouble(s ->
                            getDistance(finalCurrentY, finalCurrentX,
                                    Double.parseDouble(s.getMapY()), Double.parseDouble(s.getMapX()))
                    ));
            CentralTouristInfo nextInfo = nextSpotOptional.orElseThrow(() -> new RuntimeException("다음 관광지가 없습니다."));

            visited.add(nextInfo.getHubTatsNm());
            TourCourseDTO nextSpotDTO = createTourCourseDTO(nextInfo.getHubTatsNm(), nextInfo.getMapX(), nextInfo.getMapY());
            course.add(nextSpotDTO);

            location = getLocation(nextInfo);
        }
        return course;
    }

    // 해당 지역의 중심관광지 목록 조회
    private List<CentralTouristInfo> getCentralTouristSpots(String areaCode, String sigunguCode) {
        try {
            return centralTouristService.getCentralTouristInfo(areaCode, sigunguCode);
        } catch (RuntimeException e) {
            throw new NotFoundException("지역코드 혹은 시군구코드 입력 오류");
        }
    }

    // 관광지 정보 조회
    private CentralTouristInfo getTourInfo(String spot, String areaCode, String sigunguCode) {
        getCentralTouristSpots(areaCode, sigunguCode);
        return centralTouristInfoRepository.findByHubTatsNm(spot)
                .orElseThrow(() -> new NotFoundException("해당 관광지의 위치 정보를 찾을 수 없어 코스 추천 불가"));
    }

    // 경도, 위도 조회
    private double[] getLocation(CentralTouristInfo info) {
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
