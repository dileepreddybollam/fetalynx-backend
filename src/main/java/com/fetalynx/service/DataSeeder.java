package com.fetalynx.service;

import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fetalynx.model.AIReport;

/**
 * Seeds demo data on application startup for testing.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final PatientService patientService;
    private final ReadingService readingService;
    private final AIReportService aiReportService;

    public DataSeeder(UserService userService, PatientService patientService,
                      ReadingService readingService, AIReportService aiReportService) {
        this.userService = userService;
        this.patientService = patientService;
        this.readingService = readingService;
        this.aiReportService = aiReportService;
    }

    @Override
    public void run(String... args) {
        System.out.println("🌱 Seeding demo data...");

        // Create demo doctor user
        try {
            userService.register("demo@fetalynx.com", "demo123", "doctor");
            System.out.println("  ✅ Demo doctor created: demo@fetalynx.com / demo123");
        } catch (RuntimeException e) {
            System.out.println("  ⚠️ Demo doctor already exists");
        }

        // Create demo patient user
        try {
            userService.register("patient@fetalynx.com", "patient123", "patient");
            System.out.println("  ✅ Demo patient created: patient@fetalynx.com / patient123");
        } catch (RuntimeException e) {
            System.out.println("  ⚠️ Demo patient already exists");
        }

        // Create demo patients
        var p1 = patientService.create("Sarah Johnson", 28, "32 weeks", "First pregnancy, healthy", "demo@fetalynx.com");
        var p2 = patientService.create("Emily Davis", 31, "28 weeks", "Second pregnancy", "demo@fetalynx.com");
        var p3 = patientService.create("Maria Garcia", 25, "36 weeks", "Near full term, routine monitoring", "demo@fetalynx.com");

        // Create patients linked to the patient user account
        patientService.create("Patient Self", 28, "32 weeks", "Self-monitoring patient account", "patient@fetalynx.com");
        System.out.println("  ✅ 3 demo patients created");

        // Generate mock readings for patient 1 (300 readings ~50ms apart)
        Random random = new Random(42);
        long baseTime = System.currentTimeMillis() - 15000; // 15 seconds ago
        for (int i = 0; i < 300; i++) {
            double doppler = 1800 + random.nextGaussian() * 400; // Normal range centered at 1800
            double piezo = 800 + random.nextGaussian() * 200;
            doppler = Math.max(0, Math.min(4095, doppler));
            piezo = Math.max(0, Math.min(4095, piezo));
            readingService.addReading(p1.getId(), doppler, piezo, baseTime + (i * 50L));
        }
        System.out.println("  ✅ 300 readings seeded for " + p1.getName());

        // Generate readings for patient 2
        for (int i = 0; i < 150; i++) {
            double doppler = 2200 + random.nextGaussian() * 300;
            double piezo = 600 + random.nextGaussian() * 150;
            doppler = Math.max(0, Math.min(4095, doppler));
            piezo = Math.max(0, Math.min(4095, piezo));
            readingService.addReading(p2.getId(), doppler, piezo, baseTime + (i * 50L));
        }
        System.out.println("  ✅ 150 readings seeded for " + p2.getName());

        // Generate an AI report for patient 1
        AIReport report = new AIReport(p1.getId(),
                "Fetal monitoring analysis complete. Based on 300 readings, the fetal heart rate pattern shows " +
                "reassuring characteristics. Average Doppler reading: 1803.2 (normal range). Heart rate variability " +
                "is within expected parameters (σ=398.5). Piezoelectric contraction monitoring shows average " +
                "intensity of 801.4. No concerning patterns detected in current monitoring window.",
                List.of("LOW_RISK"),
                List.of("Continue routine monitoring schedule.", "Next assessment recommended in standard interval."));
        aiReportService.addReport(report);
        System.out.println("  ✅ Demo AI report created for " + p1.getName());

        System.out.println("🚀 Fetalynx backend ready on port 8100");
    }
}
