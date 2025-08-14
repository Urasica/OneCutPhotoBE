package com.project.tourpicture.service;

import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.dto.RelatedTourDTO;
import com.project.tourpicture.exception.NotFoundException;
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

    // 관광지별 연관관광지 조회
    public List<RelatedTourDTO> getRelatedTouristSpots(
            String areaCode, String sigunguCode, String contentId) {

        List<RegionBasedTourist> regionBasedTourists = regionBasedTouristService.getRegionBasedTouristsEntity(areaCode, sigunguCode);
        RegionBasedTourist baseSpotInfo = regionBasedTouristRepository.findByContentId(contentId)
                .orElseThrow(() -> new NotFoundException("해당 관광지의 위치 정보 없음"));

        double[] baseSpotLocation = getLocation(baseSpotInfo);
        double baseX = baseSpotLocation[0]; // 경도
        double baseY = baseSpotLocation[1]; // 위도

        return regionBasedTourists.stream()
                .filter(s -> !s.getTitle().equals(contentId))
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