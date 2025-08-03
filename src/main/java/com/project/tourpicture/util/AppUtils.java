package com.project.tourpicture.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
