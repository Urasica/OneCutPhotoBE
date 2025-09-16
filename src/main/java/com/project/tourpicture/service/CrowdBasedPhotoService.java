package com.project.tourpicture.service;

import com.project.tourpicture.dao.*;
import com.project.tourpicture.dto.RegionBasedTouristDTO;
import com.project.tourpicture.repository.CrowdBasedPhotoHighRepository;
import com.project.tourpicture.repository.CrowdBasedPhotoRepository;
import com.project.tourpicture.repository.GovernmentVisitInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrowdBasedPhotoService {

    private final GovernmentVisitInfoRepository governmentVisitInfoRepository;
    private final HubTourismService hubTourismService;
    private final CrowdBasedPhotoRepository crowdBasedPhotoRepository;
    private final CrowdBasedPhotoHighRepository crowdBasedPhotoHighRepository;

    // DB에 사진목록 설정
    @Transactional
    public void initializePhotoDB() {
        Set<String> usedContentIds = new HashSet<>();

        // 1. 집중률 낮은 지역 (Top 50 방문객 적은 순)
        List<GovernmentVisitInfo> lowVisitAreas =
                governmentVisitInfoRepository.findTop50ByOrderByTouNumAsc();

        // 2. 집중률 높은 지역 (Top 50 방문객 많은 순)
        List<GovernmentVisitInfo> highVisitAreas =
                governmentVisitInfoRepository.findTop50ByOrderByTouNumDesc();

        // -------------------- 낮은 지역 처리 --------------------
        for (GovernmentVisitInfo area : lowVisitAreas) {
            String areaCd = area.getSigunguCd().substring(0, 2);
            String sigunguCd = area.getSigunguCd().substring(2, 5);

            List<HubTourismEntity> hubs = hubTourismService.getHubTourismWithRanking(areaCd, sigunguCd);
            if (hubs.isEmpty()) continue;

            // 새 데이터 준비
            List<CrowdBasedPhoto> newPhotos = hubs.stream()
                    .sorted(Comparator.comparingInt(h -> h.getHubRank() != null ? h.getHubRank() : Integer.MAX_VALUE))
                    .limit(5)
                    .map(hub -> {
                        RegionBasedTouristDTO spot = hub.getMatchedTourist();
                        if (spot == null || usedContentIds.contains(spot.getContentId())) return null;

                        return CrowdBasedPhoto.builder()
                                .contentId(spot.getContentId())
                                .title(spot.getTitle())
                                .addr1(spot.getAddr1())
                                .addr2(spot.getAddr2())
                                .areaCd(areaCd)
                                .sigunguCd(sigunguCd)
                                .sigunguNm(area.getSigunguNm())
                                .imageUrl(spot.getFirstImage())
                                .cpyrhtDivCd(spot.getCpyrhtDivCd())
                                .modifiedTime(spot.getModifiedTime())
                                .mapX(spot.getMapX())
                                .mapY(spot.getMapY())
                                .contentTypeId(spot.getContentTypeId())
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .toList();

            if (!newPhotos.isEmpty()) {
                crowdBasedPhotoRepository.deleteByAreaCdAndSigunguCd(areaCd, sigunguCd);
                crowdBasedPhotoRepository.saveAll(newPhotos);
                newPhotos.forEach(p -> usedContentIds.add(p.getContentId()));
            }
        }

        // -------------------- 높은 지역 처리 --------------------
        for (GovernmentVisitInfo area : highVisitAreas) {
            String areaCd = area.getSigunguCd().substring(0, 2);
            String sigunguCd = area.getSigunguCd().substring(2, 5);

            List<HubTourismEntity> hubs = hubTourismService.getHubTourismWithRanking(areaCd, sigunguCd);
            if (hubs.isEmpty()) continue;

            List<CrowdBasedPhotoHigh> newPhotos = hubs.stream()
                    .sorted(Comparator.comparingInt(h -> h.getHubRank() != null ? h.getHubRank() : Integer.MAX_VALUE))
                    .limit(5)
                    .map(hub -> {
                        RegionBasedTouristDTO spot = hub.getMatchedTourist();
                        if (spot == null || usedContentIds.contains(spot.getContentId())) return null;

                        return CrowdBasedPhotoHigh.builder()
                                .contentId(spot.getContentId())
                                .title(spot.getTitle())
                                .addr1(spot.getAddr1())
                                .addr2(spot.getAddr2())
                                .areaCd(areaCd)
                                .sigunguCd(sigunguCd)
                                .sigunguNm(area.getSigunguNm())
                                .imageUrl(spot.getFirstImage())
                                .cpyrhtDivCd(spot.getCpyrhtDivCd())
                                .modifiedTime(spot.getModifiedTime())
                                .mapX(spot.getMapX())
                                .mapY(spot.getMapY())
                                .contentTypeId(spot.getContentTypeId())
                                .build();
                    })
                    .filter(Objects::nonNull)
                    .toList();

            if (!newPhotos.isEmpty()) {
                crowdBasedPhotoHighRepository.deleteByAreaCdAndSigunguCd(areaCd, sigunguCd);
                crowdBasedPhotoHighRepository.saveAll(newPhotos);
                newPhotos.forEach(p -> usedContentIds.add(p.getContentId()));
            }
        }

        // 선정 지역 코드 집합
        Set<String> selectedAreaCodes = Stream.concat(lowVisitAreas.stream(), highVisitAreas.stream())
                .map(GovernmentVisitInfo::getSigunguCd) // 전체 시군구 코드, 예: "54382"
                .collect(Collectors.toSet());

        // 지역별 사진 저장 후, 선정 지역 외 삭제
        crowdBasedPhotoRepository.deleteByAreaCdSigunguCdNotIn(selectedAreaCodes);
        crowdBasedPhotoHighRepository.deleteByAreaCdSigunguCdNotIn(selectedAreaCodes);

        log.info("선정된 지역 외 CrowdBasedPhoto 및 CrowdBasedPhotoHigh 삭제 완료");
    }

    public List<CrowdBasedPhoto> getCrowdBasedPhotos() {
        List<CrowdBasedPhoto> allPhotos = crowdBasedPhotoRepository.findAll();
        if (allPhotos.isEmpty()) return Collections.emptyList();

        Collections.shuffle(allPhotos);
        return allPhotos;
    }

    public List<CrowdBasedPhotoHigh> getCrowdBasedPhotosHigh() {
        List<CrowdBasedPhotoHigh> allPhotos = crowdBasedPhotoHighRepository.findAll();
        if (allPhotos.isEmpty()) return Collections.emptyList();

        Collections.shuffle(allPhotos);
        return allPhotos;
    }
}