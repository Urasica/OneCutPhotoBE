package com.project.tourpicture.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class AreaDTO {
    private String areaCd;
    private String areaNm;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AreaDTO dto)) return false;
        return Objects.equals(areaCd, dto.areaCd) &&
                Objects.equals(areaNm, dto.areaNm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(areaCd, areaNm);
    }
}
