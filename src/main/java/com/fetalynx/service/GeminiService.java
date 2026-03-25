package com.fetalynx.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class GeminiService {

    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public GeminiService(@Value("${gemini.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
        this.mapper = new ObjectMapper();
    }

    /**
     * Generate a fetal health analysis report using Gemini.
     *
     * @param gestationalAge e.g. "32 weeks"
     * @param pregnancyNotes e.g. "First pregnancy, healthy"
     * @param avgDoppler     average Doppler reading
     * @param avgPiezo       average Piezo reading
     * @param dopplerStdDev  standard deviation of Doppler
     * @param piezoStdDev    standard deviation of Piezo
     * @param maxDoppler     maximum Doppler value
     * @param minDoppler     minimum Doppler value
     * @param readingCount   number of readings analysed
     * @return A structured 5-line report from Gemini
     */
    public String analyseSignals(String gestationalAge, String pregnancyNotes,
                                  double avgDoppler, double avgPiezo,
                                  double dopplerStdDev, double piezoStdDev,
                                  double maxDoppler, double minDoppler,
                                  int readingCount) {
        try {
            String prompt = buildPrompt(gestationalAge, pregnancyNotes,
                    avgDoppler, avgPiezo, dopplerStdDev, piezoStdDev,
                    maxDoppler, minDoppler, readingCount);

            ObjectNode requestBody = mapper.createObjectNode();
            ArrayNode contents = requestBody.putArray("contents");
            ObjectNode content = contents.addObject();
            ArrayNode parts = content.putArray("parts");
            ObjectNode textPart = parts.addObject();
            textPart.put("text", prompt);

            // Safety settings — be less restrictive for medical content
            ObjectNode generationConfig = requestBody.putObject("generationConfig");
            generationConfig.put("temperature", 0.3);
            generationConfig.put("maxOutputTokens", 500);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + apiKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                return "GEMINI_ERROR: API returned status " + response.statusCode() + ". Falling back to local analysis.";
            }

            JsonNode root = mapper.readTree(response.body());
            JsonNode candidates = root.path("candidates");
            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode text = candidates.get(0).path("content").path("parts").get(0).path("text");
                return text.asText().trim();
            }
            return "GEMINI_ERROR: No candidates in response.";

        } catch (Exception e) {
            return "GEMINI_ERROR: " + e.getMessage();
        }
    }

    private String buildPrompt(String gestationalAge, String pregnancyNotes,
                                double avgDoppler, double avgPiezo,
                                double dopplerStdDev, double piezoStdDev,
                                double maxDoppler, double minDoppler,
                                int readingCount) {
        return """
                You are a fetal health analysis AI assistant integrated into the Fetalynx monitoring platform.
                Analyse the following sensor data from a fetal monitoring session and provide a clinical report.

                === PATIENT CONTEXT ===
                Gestational Age: %s
                Clinical Notes: %s

                === SENSOR DATA SUMMARY (%d readings) ===
                Doppler Ultrasound (fetal heart rate proxy):
                  - Average: %.1f
                  - Std Deviation: %.1f
                  - Range: %.1f – %.1f

                Piezoelectric Sensor (uterine activity / fetal movement):
                  - Average: %.1f
                  - Std Deviation: %.1f

                === INSTRUCTIONS ===
                Provide EXACTLY 5 lines in this format:
                Line 1: VERDICT: [HEALTHY / CAUTION / CONCERNING / CRITICAL]
                Line 2: Fetal heart rate assessment (one sentence)
                Line 3: Uterine activity / movement assessment (one sentence)
                Line 4: Key risk factor or reassuring finding (one sentence)
                Line 5: Recommended action (one sentence)

                Be concise and clinical. Do NOT add extra lines, headers, or explanations.
                """.formatted(
                gestationalAge, pregnancyNotes,
                readingCount,
                avgDoppler, dopplerStdDev, minDoppler, maxDoppler,
                avgPiezo, piezoStdDev
        );
    }
}
