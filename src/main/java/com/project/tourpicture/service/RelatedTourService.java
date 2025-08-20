package com.project.tourpicture.service;

import com.project.tourpicture.dao.KeywordBasedTourist;
import com.project.tourpicture.dao.RegionBasedTourist;
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
    private final KeywordBasedTouristRepository keywordBasedTouristRepository;

    // 관광지별 연관관광지 조회
    public List<RelatedTourDTO> getRelatedTouristSpots(
            String areaCode, String sigunguCode, String contentId) {

        double baseX;
        double baseY;

        List<RegionBasedTourist> regionBasedTourists = regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode, 12);
        regionBasedTourists.addAll(regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode, 14));

        RegionBasedTourist baseSpotInfo = regionBasedTouristRepository.findByContentId(contentId);

        if (baseSpotInfo != null) {
            baseX = Double.parseDouble(baseSpotInfo.getMapX()); //경도
            baseY = Double.parseDouble(baseSpotInfo.getMapY()); //위도
        } else {
            KeywordBasedTourist keywordBasedTourist = keywordBasedTouristRepository.findByContentId(contentId)
                    .orElseThrow(() -> new NotFoundException("해당 관광지의 위치 정보 없음"));
            baseX = Double.parseDouble(keywordBasedTourist.getMapX()); //경도
            baseY = Double.parseDouble(keywordBasedTourist.getMapY()); //위도
        }

        return regionBasedTourists.stream()
                .filter(s -> !s.getContentId().equals(contentId))
                .sorted(Comparator.comparingDouble(s -> {
                    double[] location = getLocation(s);
                    return getDistance(baseY, baseX, location[1], location[0]);
                }))
                .limit(30)
                .map(s -> createRelatedTourDTO(
                        s.getContentId(),
                        s.getContentTypeId(),
                        s.getTitle(),
                        s.getFirstImage(),
                        s.getAddr1(),
                        s.getMapX(),
                        s.getMapY()
                ))
                .toList();
    }

    private RelatedTourDTO createRelatedTourDTO(String contentId, String contentTypeId, String title,
                                                String imageUrl, String address, String mapX, String mapY) {
        RelatedTourDTO relatedTourDTO = new RelatedTourDTO();
        relatedTourDTO.setContentId(contentId);
        relatedTourDTO.setContentTypeId(contentTypeId);
        relatedTourDTO.setTitle(title);
        relatedTourDTO.setImageUrl(imageUrl);
        relatedTourDTO.setAddress(address);
        relatedTourDTO.setMapX(mapX);
        relatedTourDTO.setMapY(mapY);
        return relatedTourDTO;
    }
}