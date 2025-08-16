package com.project.tourpicture.service;

import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.dto.RestaurantDTO;
import com.project.tourpicture.exception.NotFoundException;
import com.project.tourpicture.repository.RegionBasedTouristRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RestaurantInfoService {

    private final RegionBasedTouristService regionBasedTouristService;
    private final RegionBasedTouristRepository regionBasedTouristRepository;

    // 해당 지역의 식당 리스트 반환
    public List<RestaurantDTO> getRestaurants(String areaCode, String sigunguCode) {

        List<RegionBasedTourist> restaurants = regionBasedTouristRepository.findByAreaCdAndSigunguCdAndContentTypeId(
                areaCode, sigunguCode, "39");

        List<RestaurantDTO> DTOs = new ArrayList<>();
        if(restaurants.isEmpty()){
            getRestaurantDTOList(areaCode, sigunguCode, DTOs);
            return DTOs;
        }

        if(restaurants.get(0).getUpdatedAt().isBefore(LocalDateTime.now().minusDays(7))){
            regionBasedTouristRepository.deleteByAreaCdAndSigunguCdAndContentTypeId(areaCode, sigunguCode, "39");
            getRestaurantDTOList(areaCode, sigunguCode, DTOs);
            return DTOs;
        }

        for (RegionBasedTourist restaurant : restaurants) {
            createRestaurantDTO(restaurant, DTOs);
        }
        return DTOs;
    }

    // 식당 DTO 리스트 반환
    private void getRestaurantDTOList(String areaCode, String sigunguCode, List<RestaurantDTO> DTOs) {
        List<RegionBasedTourist> regionBasedRestaurants = regionBasedTouristService.getRegionBasedTouristsEntity(
                areaCode, sigunguCode, 39);

        if (regionBasedRestaurants == null || regionBasedRestaurants.isEmpty()) {
            throw new NotFoundException("해당 지역의 식당 정보 없음");
        }

        for (RegionBasedTourist restaurant : regionBasedRestaurants) {
            createRestaurantDTO(restaurant, DTOs);
        }
    }

    // 식당 DTO 생성
    private void createRestaurantDTO(RegionBasedTourist restaurant, List<RestaurantDTO> DTOs) {
        RestaurantDTO dto = new RestaurantDTO();
        dto.setName(restaurant.getTitle());
        dto.setAddress(restaurant.getAddr1());
        dto.setMapX(restaurant.getMapX());
        dto.setMapY(restaurant.getMapY());
        dto.setImageUrl(restaurant.getFirstImage());
        DTOs.add(dto);
    }
}
