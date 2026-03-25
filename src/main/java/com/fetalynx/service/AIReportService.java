package com.fetalynx.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.fetalynx.model.AIReport;

@Service
public class AIReportService {

    private final Map<String, List<AIReport>> reportsByPatient = new ConcurrentHashMap<>();
    private final ReadingService readingService;

    public AIReportService(ReadingService readingService) {
        this.readingService = readingService;
    }

    /**
     * Generate a mock AI clinical report based on reading statistics.
     * In production, this would call the Gemini API.
     */
    public AIReport generateReport(String patientId) {
        Map<String, Object> stats = readingService.getAggregatedStats(patientId, 300);
        int count = (int) stats.getOrDefault("count", 0);

        String summary;
        List<String> riskIndicators;
        List<String> recommendations;

        if (count == 0) {
            summary = "Insufficient data for analysis. No sensor readings found for this patient.";
            riskIndicators = List.of("NO_DATA");
            recommendations = List.of("Ensure ESP32 device is connected and transmitting data.",
                    "Verify patient ID configuration on the device.");
        } else {
            double avgDoppler = (double) stats.getOrDefault("avgDoppler", 0.0);
            double avgPiezo = (double) stats.getOrDefault("avgPiezo", 0.0);
            double dopplerVar = (double) stats.getOrDefault("dopplerVariability", 0.0);
            double maxDoppler = (double) stats.getOrDefault("maxDoppler", 0.0);

            // Simulate clinical analysis based on readings
            boolean normalRange = avgDoppler > 1000 && avgDoppler < 3000;
            boolean goodVariability = dopplerVar > 100;
            boolean noPeakConcern = maxDoppler < 3800;

            List<String> risks = new ArrayList<>();
            List<String> recs = new ArrayList<>();

            if (normalRange && goodVariability && noPeakConcern) {
                summary = String.format(
                    "Fetal monitoring analysis complete. Based on %d readings, the fetal heart rate pattern " +
                    "shows reassuring characteristics. Average Doppler reading: %.1f (normal range). " +
                    "Heart rate variability is within expected parameters (σ=%.1f). " +
                    "Piezoelectric contraction monitoring shows average intensity of %.1f. " +
                    "No concerning patterns detected in current monitoring window.",
                    count, avgDoppler, dopplerVar, avgPiezo);
                risks.add("LOW_RISK");
                recs.add("Continue routine monitoring schedule.");
                recs.add("Next assessment recommended in standard interval.");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("Fetal monitoring analysis based on %d readings. ", count));

                if (!normalRange) {
                    sb.append(String.format("Average Doppler reading (%.1f) is outside normal range. ", avgDoppler));
                    risks.add("ABNORMAL_FHR_BASELINE");
                    recs.add("Recommend closer monitoring intervals.");
                }
                if (!goodVariability) {
                    sb.append(String.format("Reduced heart rate variability detected (σ=%.1f). ", dopplerVar));
                    risks.add("REDUCED_VARIABILITY");
                    recs.add("Consider fetal stimulation test.");
                }
                if (!noPeakConcern) {
                    sb.append(String.format("Peak Doppler value (%.1f) exceeds threshold. ", maxDoppler));
                    risks.add("HIGH_PEAK_VALUES");
                    recs.add("Review for potential acceleration patterns.");
                }

                sb.append("Clinical correlation advised.");
                summary = sb.toString();
                recs.add("Consult attending physician for comprehensive evaluation.");
            }

            riskIndicators = risks;
            recommendations = recs;
        }

        AIReport report = new AIReport(patientId, summary, riskIndicators, recommendations);
        reportsByPatient
                .computeIfAbsent(patientId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(report);

        return report;
    }

    public List<AIReport> getReports(String patientId) {
        List<AIReport> reports = reportsByPatient.getOrDefault(patientId, Collections.emptyList());
        List<AIReport> result = new ArrayList<>(reports);
        result.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        return result;
    }

    public void addReport(AIReport report) {
        reportsByPatient
                .computeIfAbsent(report.getPatientId(), k -> Collections.synchronizedList(new ArrayList<>()))
                .add(report);
    }
}
