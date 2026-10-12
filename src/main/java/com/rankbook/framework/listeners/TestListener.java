package com.rankbook.framework.listeners;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.rankbook.framework.utils.ExtentReportManager;
import com.rankbook.framework.utils.ScreenshotUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        ExtentReportManager.initReport();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentReportManager.flushReport();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.startTest(testName);
        
        String description = result.getMethod().getDescription();
        if (description != null && !description.isBlank()) {
            ExtentReportManager.getTest().info("Description: " + description);
        }
        ExtentReportManager.getTest().log(Status.INFO, "Execution started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest().log(Status.PASS, "Test PASSED: " + result.getMethod().getMethodName());
        ExtentReportManager.unloadTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentReportManager.getTest().log(Status.FAIL, "Test FAILED: " + testName);
        ExtentReportManager.getTest().log(Status.FAIL, result.getThrowable());

        try {
            // Auto-capture screenshot on failure & attach to ExtentReport
            String base64Image = ScreenshotUtils.captureBase64Screenshot();
            if (base64Image != null && !base64Image.isBlank()) {
                // Pass raw base64 string directly (do NOT prepend "data:image/png;base64,")
                ExtentReportManager.getTest().fail(
                    "Failure Screenshot:",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64Image).build()
                );
            }
        } catch (Exception e) {
            ExtentReportManager.getTest().warning("Failed to attach failure screenshot: " + e.getMessage());
        } finally {
            ExtentReportManager.unloadTest();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.getTest().log(Status.SKIP, "Test SKIPPED: " + result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            ExtentReportManager.getTest().skip(result.getThrowable());
        }
        ExtentReportManager.unloadTest();
    }
}