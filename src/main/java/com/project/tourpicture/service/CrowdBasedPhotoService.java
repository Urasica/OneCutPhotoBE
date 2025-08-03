package com.project.tourpicture.service;

import com.project.tourpicture.dao.CrowdBasedPhoto;
import com.project.tourpicture.dao.GovernmentVisitInfo;
import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.repository.CrowdBasedPhotoRepository;
import com.project.tourpicture.repository.GovernmentVisitInfoRepository;
import com.project.tourpicture.repository.RegionBasedTouristRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrowdBasedPhotoService {
    private static final int MAX_SIZE = 150;

    private final GovernmentVisitInfoRepository governmentVisitInfoRepository;
    private final RegionBasedTouristRepository regionBasedTouristRepository;
    private final CrowdBasedPhotoRepository crowdBasedPhotoRepository;
    private final RegionBasedTouristService regionBasedTouristService;


    // DB에 사진목록 설정
    @Transactional
    public void initializePhotoDB() {
        Set<String> usedContentIds = new HashSet<>();
        List<GovernmentVisitInfo> lowVisitAreas = governmentVisitInfoRepository.findTop30ByOrderByTouNumAsc();
        List<RegionBasedTourist> candidateSpots = new ArrayList<>();

        for (GovernmentVisitInfo area : lowVisitAreas) {
            String areaCd = area.getSigunguCd().substring(0, 2);
            String sigunguCd = area.getSigunguCd().substring(2, 5);

            List<RegionBasedTourist> allSpots = regionBasedTouristRepository
                    .findByAreaCdAndSigunguCd(areaCd, sigunguCd);

            if (allSpots.isEmpty()) {
                allSpots = regionBasedTouristService.getRegionBasedTouristsEntity(areaCd, sigunguCd);
            }

            List<RegionBasedTourist> filtered = allSpots.stream()
                    .filter(spot -> spot.getFirstImage() != null && !spot.getFirstImage().isBlank())
                    .collect(Collectors.toList());

            Collections.shuffle(filtered);

            // 최대 5개만 추출
            candidateSpots.addAll(filtered.stream().limit(5).toList());
        }

        // 전체 후보 섞고 MAX_SIZE 만큼 저장
        Collections.shuffle(candidateSpots);

        for (RegionBasedTourist spot : candidateSpots) {
            String contentId = spot.getContentId();
            if (usedContentIds.contains(contentId)) continue;

            // GovernmentVisitInfo로부터 signguNm 가져오기
            String regionCode = spot.getAreaCd() + spot.getSigunguCd();

            GovernmentVisitInfo matchedArea = lowVisitAreas.stream()
                    .filter(a -> a.getSigunguCd().equals(regionCode))
                    .findFirst()
                    .orElse(null);

            if (matchedArea == null) {
                log.warn("No match for regionCode={}, spot: areaCd={}, sigunguCd={}",
                        regionCode, spot.getAreaCd(), spot.getSigunguCd());
                continue;
            }

            CrowdBasedPhoto photo = CrowdBasedPhoto.builder()
                    .contentId(contentId)
                    .title(spot.getTitle())
                    .addr1(spot.getAddr1())
                    .addr2(spot.getAddr2())
                    .areaCd(spot.getAreaCd())
                    .sigunguCd(spot.getSigunguCd())
                    .sigunguNm(matchedArea.getSigunguNm())
                    .imageUrl(spot.getFirstImage())
                    .cpyrhtDivCd(spot.getCpyrhtDivCd())
                    .modifiedTime(spot.getModifiedTime())
                    .build();

            try {
                crowdBasedPhotoRepository.save(photo);
                usedContentIds.add(contentId);
                if (usedContentIds.size() >= MAX_SIZE) break;
            } catch (Exception e) {
                log.warn("사진 저장 실패: contentId={}, title={}", contentId, spot.getTitle(), e);
            }
        }
    }

    public List<CrowdBasedPhoto> getCrowdBasedPhotos(Set<String> seenIds, int limit) {
        // 전체 contentId 조회 및 랜덤 셔플
        List<String> allIds = crowdBasedPhotoRepository.findAllContentIds();
        if (allIds.isEmpty()) return Collections.emptyList();

        List<String> filtered = allIds.stream()
                .filter(id -> seenIds == null || !seenIds.contains(id))
                .collect(Collectors.toList());

        // seenIds로 인해 가져올 수 있는 ID가 부족하면 종료
        if (filtered.size() < limit) return Collections.emptyList();

        // 셔플 후 limit만큼 추출
        Collections.shuffle(filtered);
        List<String> targetIds = filtered.subList(0, limit);

        return crowdBasedPhotoRepository.findByContentIdIn(targetIds);
    }
}