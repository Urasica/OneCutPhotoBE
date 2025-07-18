package com.project.tourpicture.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class MetropolitanLocalGovernmentFocusInfoId implements Serializable {
    private String baseYmd;
    private String touDivCd;
    private String areaCode;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetropolitanLocalGovernmentFocusInfoId that)) return false;
        return Objects.equals(baseYmd, that.baseYmd)
                && Objects.equals(touDivCd, that.touDivCd)
                && Objects.equals(areaCode, that.areaCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseYmd, touDivCd, areaCode);
    }
}
