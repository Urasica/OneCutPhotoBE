package com.project.tourpicture.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class BasicLocalGovernmentFocusInfoId implements Serializable {
    private String baseYmd;
    private String touDivCd;
    private String signguCd;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BasicLocalGovernmentFocusInfoId that)) return false;
        return Objects.equals(baseYmd, that.baseYmd)
                && Objects.equals(touDivCd, that.touDivCd)
                && Objects.equals(signguCd, that.signguCd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseYmd, touDivCd, signguCd);
    }
}
