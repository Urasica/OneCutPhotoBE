package com.project.tourpicture.service;

import com.project.tourpicture.dao.KeywordBasedTourist;
import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.dto.TouristLocationDTO;
import com.project.tourpicture.dto.RelatedTourDTO;
import com.project.tourpicture.exception.NotFoundException;
import com.project.tourpicture.repository.KeywordBasedTouristRepository;
import com.project.tourpicture.repository.RegionBasedTouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

import static com.project.tourpicture.util.AppUtils.getDistance;
import static com.project.tourpicture.util.AppUtils.getLocation;

@Service
@RequiredArgsConstructor
public class RelatedTourService {

    private final RegionBasedTouristRepository regionBasedTouristRepository;
    private final RegionBasedTouristService regionBasedTouristService;
    private final KeywordBasedTouristService keywordBasedTouristService;
    private final KeywordBasedTouristRepository keywordBasedTouristRepository;

    // 관광지별 연관관광지 조회
    public List<RelatedTourDTO> getRelatedTouristSpots(
            String areaCode, String sigunguCode, String contentId, String keyword) {

        // 지역 기반 관광지 리스트
        List<RegionBasedTourist> regionBasedTourists = regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode, 12);
        regionBasedTourists.addAll(regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode, 14));

        TouristLocationDTO touristLocation;
        RegionBasedTourist regionBasedTourist = regionBasedTouristRepository.findByContentId(contentId);

        if(regionBasedTourist != null) {
            touristLocation = new TouristLocationDTO(
                    regionBasedTourist.getTitle(),
                    regionBasedTourist.getMapX(), regionBasedTourist.getMapY());
        } else {
            keywordBasedTouristService.getKeywordBasedTourists(keyword);
            KeywordBasedTourist keywordBasedTourist = keywordBasedTouristRepository.findByContentId(contentId)
                    .orElseThrow(() -> new NotFoundException("해당 관광지의 위치 정보 없음"));
            touristLocation = new TouristLocationDTO(
                    keywordBasedTourist.getTitle(),
                    keywordBasedTourist.getMapX(), keywordBasedTourist.getMapY());
        }

        double baseX = getLocation(touristLocation)[0]; // 경도
        double baseY = getLocation(touristLocation)[1]; // 위도

        return regionBasedTourists.stream()
                .filter(s -> !s.getContentId().equals(contentId))
                .sorted(Comparator.comparingDouble(s -> getDistance(baseY, baseX,
                        Double.parseDouble(s.getMapY()), Double.parseDouble(s.getMapX()))))
                .limit(30)
                .map(s -> createRelatedTourDTO(
                        s.getContentId(),
                        s.getContentTypeId(),
                        s.getTitle(),
                        s.getFirstImage(),
                        s.getAddr1()
                ))
                .toList();
    }

    private RelatedTourDTO createRelatedTourDTO(String contentId, String contentTypeId, String title,
                                               String imageUrl, String address) {
        RelatedTourDTO relatedTourDTO = new RelatedTourDTO();
        relatedTourDTO.setContentId(contentId);
        relatedTourDTO.setContentTypeId(contentTypeId);
        relatedTourDTO.setTitle(title);
        relatedTourDTO.setImageUrl(imageUrl);
        relatedTourDTO.setAddress(address);
        return relatedTourDTO;
    }
}