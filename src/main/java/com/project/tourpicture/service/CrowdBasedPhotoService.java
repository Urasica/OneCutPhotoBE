package com.project.tourpicture.service;

import com.project.tourpicture.dao.CentralTouristInfo;
import com.project.tourpicture.dao.CrowdBasedPhoto;
import com.project.tourpicture.dao.GovernmentVisitInfo;
import com.project.tourpicture.dao.RelatedTourPhoto;
import com.project.tourpicture.dto.CrowdBasedPhotoResponseDTO;
import com.project.tourpicture.dto.TourPhotoDTO;
import com.project.tourpicture.repository.CentralTouristInfoRepository;
import com.project.tourpicture.repository.CrowdBasedPhotoRepository;
import com.project.tourpicture.repository.GovernmentVisitInfoRepository;
import com.project.tourpicture.repository.RelatedTourPhotoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CrowdBasedPhotoService {
    private static final int MAX_SIZE = 50;

    @Autowired
    private GovernmentVisitInfoRepository governmentVisitInfoRepository;
    @Autowired
    private CentralTouristInfoRepository centralTouristInfoRepository;
    @Autowired
    private RelatedTourPhotoRepository relatedTourPhotoRepository;
    @Autowired
    private TourInfoService tourInfoService;
    @Autowired
    private CrowdBasedPhotoRepository crowdBasedPhotoRepository;

    // DB에 사진목록 설정
    public void initializePhotoDB() {
        Set<Long> usedPhotoIds = new HashSet<>();
        List<GovernmentVisitInfo> lowVisitAreas = governmentVisitInfoRepository.findTop30ByOrderByTouNumAsc();
        List<TouristAreaEntry> allTouristEntries = new ArrayList<>();

        for (GovernmentVisitInfo area : lowVisitAreas) {
            List<CentralTouristInfo> touristSpots =
                    centralTouristInfoRepository.findTop5BySignguCdOrderByHubRankAsc(area.getSignguCd());

            for (CentralTouristInfo spot : touristSpots) {
                if (!"관광지".equals(spot.getHubCtgryLclsNm())) continue; // 관광지만 필터링
                allTouristEntries.add(new TouristAreaEntry(area, spot));
            }
        }

        Collections.shuffle(allTouristEntries);

        for (TouristAreaEntry entry : allTouristEntries) {
            GovernmentVisitInfo area = entry.area();
            CentralTouristInfo spot = entry.spot();

            Optional<RelatedTourPhoto> photoOpt =
                    relatedTourPhotoRepository.findFirstByOriginalContaining(spot.getHubTatsNm());

            String imageUrl;
            String takenMonth;
            long photoId;

            if (photoOpt.isPresent()) {
                RelatedTourPhoto photo = photoOpt.get();
                if (usedPhotoIds.contains(photo.getId())) continue;

                imageUrl = photo.getImageUrl();
                takenMonth = photo.getTakenMonth();
                photoId = photo.getId();
            } else {
                TourPhotoDTO dto;
                try {
                    dto = tourInfoService.getTourPhoto(spot.getHubTatsNm());
                } catch (Exception e) {
                    log.warn("외부 사진 조회 실패: {}", spot.getHubTatsNm());
                    continue;
                }

                if (dto == null) continue;

                imageUrl = dto.getImageUrl();
                takenMonth = dto.getTakenMonth();

                // hash → 양수화 (음수 방지)
                photoId = Math.abs(Objects.hash(dto.getImageUrl(), spot.getHubTatsNm()));
            }

            if (usedPhotoIds.contains(photoId)) continue;
            usedPhotoIds.add(photoId);

            CrowdBasedPhoto entity = CrowdBasedPhoto.builder()
                    .photoId(photoId)
                    .imageUrl(imageUrl)
                    .takenMonth(takenMonth)
                    .signguCd(area.getSignguCd())
                    .signguNm(area.getSignguNm())
                    .hubTatsCd(spot.getHubTatsCd())
                    .hubTatsNm(spot.getHubTatsNm())
                    .hubCtgryLclsNm(spot.getHubCtgryLclsNm())
                    .build();

            try {
                crowdBasedPhotoRepository.save(entity);
            } catch (Exception e) {
                log.warn("사진 저장 실패: photoId={} 관광지명={}", photoId, spot.getHubTatsNm());
            }

            if (usedPhotoIds.size() >= MAX_SIZE) return;
        }
    }

    public List<CrowdBasedPhotoResponseDTO> getCrowdBasedPhotos(Set<Long> seenIds, int limit) {
        // 1. 전체 ID 조회 및 랜덤 셔플
        List<Long> allIds = crowdBasedPhotoRepository.findAllIds(); // 커스텀 쿼리 필요
        Collections.shuffle(allIds);

        // 2. seenIds 제외 후 limit만큼 추출
        List<Long> targetIds = allIds.stream()
                .filter(id -> seenIds == null || !seenIds.contains(id))
                .limit(limit)
                .collect(Collectors.toList());

        if (targetIds.isEmpty()) return Collections.emptyList();

        // 3. 해당 ID로 실제 엔티티 조회
        List<CrowdBasedPhoto> entities = crowdBasedPhotoRepository.findByPhotoIdIn(targetIds);

        return entities.stream()
                .map(p -> CrowdBasedPhotoResponseDTO.builder()
                        .photoId(p.getPhotoId())
                        .imageUrl(p.getImageUrl())
                        .takenMonth(p.getTakenMonth())
                        .signguCd(p.getSignguCd())
                        .signguNm(p.getSignguNm())
                        .hubTatsCd(p.getHubTatsCd())
                        .hubTatsNm(p.getHubTatsNm())
                        .hubCtgryLclsNm(p.getHubCtgryLclsNm())
                        .build())
                .collect(Collectors.toList());
    }

    public record TouristAreaEntry(
            GovernmentVisitInfo area,
            CentralTouristInfo spot
    ) {}
}