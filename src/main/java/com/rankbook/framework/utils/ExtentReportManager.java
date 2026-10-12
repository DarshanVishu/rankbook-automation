package com.rankbook.framework.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public class ExtentReportManager {
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();

    public static synchronized ExtentReports initReport() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/reports/ExtentReport.html";
            File reportDir = new File(System.getProperty("user.dir") + "/reports");
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setDocumentTitle("RankBook Automation Execution Report");
            spark.config().setReportName("RankBook CRM & LMS Test Suite");
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Framework", "Hybrid (Selenium + Playwright + RestAssured)");
            extent.setSystemInfo("Target Platform", "Next.js App Router + Supabase");
            extent.setSystemInfo("QA Engineer", "SDET Automation Lead");
        }
        return extent;
    }

    public static synchronized void flushReport() {
        if (extent != null) {
            extent.flush();
        }
    }

    public static synchronized void startTest(String testName) {
        ExtentTest test = extent.createTest(testName);
        testThread.set(test);
    }

    public static ExtentTest getTest() {
        return testThread.get();
    }

    public static void unloadTest() {
        testThread.remove();
    }
}