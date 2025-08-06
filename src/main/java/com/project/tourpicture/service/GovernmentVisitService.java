package com.project.tourpicture.service;

import com.project.tourpicture.dao.BasicLocalGovernmentFocusInfo;
import com.project.tourpicture.dao.GovernmentVisitInfo;
import com.project.tourpicture.repository.BasicLocalGovernmentFocusInfoRepository;
import com.project.tourpicture.repository.GovernmentVisitInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GovernmentVisitService {

    private final BasicLocalGovernmentFocusInfoRepository basicLocalGovernmentFocusInfoRepository;

    private final GovernmentVisitInfoRepository governmentVisitInfoRepository;

    public void VisitTouristUpdate() {
        // 시군구 기준으로 그룹화 및 관광객 수 합산
        Map<String, List<BasicLocalGovernmentFocusInfo>> grouped = basicLocalGovernmentFocusInfoRepository.findAll().stream()
                .collect(Collectors.groupingBy(info ->
                        info.getBaseYmd() + "|" + info.getSigunguCd() + "|" + info.getDaywkDivCd()
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
            info.setSigunguCd(sample.getSigunguCd());
            info.setSigunguNm(sample.getSigunguNm());
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
}
