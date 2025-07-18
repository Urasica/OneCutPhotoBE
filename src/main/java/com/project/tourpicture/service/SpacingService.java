package com.project.tourpicture.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
public class SpacingService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String spacingWord(String word) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            String jsonInput = String.format("{\"text\":\"%s\"}", word);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:5000/spacing"))
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (response.statusCode() == 200) {
                String body = response.body();

                // JSON 파싱
                JsonNode root = objectMapper.readTree(body);
                return root.get("result").asText();

            } else {
                return "Error: HTTP " + response.statusCode();
            }
        } catch (Exception e) {
            return "Exception: " + e.getMessage();
        }
    }
}
