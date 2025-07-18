package com.project.tourpicture.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "HTTP 상태코드", example = "404")
    private int status;

    @Schema(description = "에러 메시지", example = "[]")
    private String message;
}
