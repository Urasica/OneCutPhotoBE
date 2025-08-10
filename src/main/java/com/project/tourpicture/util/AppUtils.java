package com.project.tourpicture.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tourpicture.dao.RegionBasedTourist;
import com.project.tourpicture.dto.ErrorResponse;
import com.project.tourpicture.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class AppUtils {

    // JsonNode 아이템 조회
    public static JsonNode getItemsNode(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        return root.path("response").path("body").path("items").path("item");
    }

    // 관광지간 거리 계산
    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // 관광지 경도, 위도 조회
    public static double[] getLocation(RegionBasedTourist info) {
        return new double[]{
                Double.parseDouble(info.getMapX()), //경도
                Double.parseDouble(info.getMapY())  //위도
        };
    }

    // 예외 처리
    public static ResponseEntity<ErrorResponse> getErrorResponse(Exception e) {
        int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        String reason = e.getMessage();

        if (e instanceof NotFoundException) {
            statusCode = HttpStatus.NOT_FOUND.value();
        }

        ErrorResponse error = new ErrorResponse(statusCode, reason);
        return ResponseEntity.status(statusCode).body(error);
    }
}
