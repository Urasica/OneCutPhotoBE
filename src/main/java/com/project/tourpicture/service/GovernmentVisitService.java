package com.project.tourpicture.service;

import com.project.tourpicture.dao.BasicLocalGovernmentFocusInfo;
import com.project.tourpicture.dao.GovernmentVisitInfo;
import com.project.tourpicture.repository.BasicLocalGovernmentFocusInfoRepository;
import com.project.tourpicture.repository.GovernmentVisitInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GovernmentVisitService {
    @Autowired
    private BasicLocalGovernmentFocusInfoRepository basicLocalGovernmentFocusInfoRepository;

    @Autowired
    private GovernmentVisitInfoRepository governmentVisitInfoRepository;

    @Autowired
    private CentralTouristService centralTouristService;

    public void VisitTouristUpdate() {
        // 시군구 기준으로 그룹화 및 관광객 수 합산
        Map<String, List<BasicLocalGovernmentFocusInfo>> grouped = basicLocalGovernmentFocusInfoRepository.findAll().stream()
                .collect(Collectors.groupingBy(info ->
                        info.getBaseYmd() + "|" + info.getSignguCd() + "|" + info.getDaywkDivCd()
                ));

        List<GovernmentVisitInfo> result = new ArrayList<>();

        // 합산한 관광객 수 저장
        for (Map.Entry<String, List<BasicLocalGovernmentFocusInfo>> entry : grouped.entrySet()) {
            List<BasicLocalGovernmentFocusInfo> group = entry.getValue();
            BasicLocalGovernmentFocusInfo sample = group.get(0);

            double totalTourists = group.stream()
                    .mapToDouble(BasicLocalGovernmentFocusInfo::getTouNum)
                    .sum();

            GovernmentVisitInfo info = new GovernmentVisitInfo();
            info.setBaseYmd(sample.getBaseYmd());
            info.setSignguCd(sample.getSignguCd());
            info.setSignguNm(sample.getSignguNm());
            info.setDaywkDivCd(sample.getDaywkDivCd());
            info.setDaywkDivNm(sample.getDaywkDivNm());
            info.setTouNum(totalTourists);

            result.add(info);
        }

        if (!result.isEmpty()) {
            governmentVisitInfoRepository.deleteAll();
            governmentVisitInfoRepository.saveAll(result);
        }
    }

    // 관광객 집중률이 적은 순으로 지역 가져오기
    public void fetchLowVisitCentralTourist() {
        String baseYmd = LocalDate.now(ZoneId.of("Asia/Seoul"))
                .minusMonths(2)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        List<GovernmentVisitInfo> lowVisits =
                governmentVisitInfoRepository.findTop30ByBaseYmdOrderByTouNumAsc(baseYmd);

        for (GovernmentVisitInfo info : lowVisits) {
            String areaCd = info.getSignguCd().substring(0, 2);
            centralTouristService.fetchCentralTouristInfo(areaCd, info.getSignguCd());
        }
    }
}
