package com.rankbook.framework.constants;

import java.time.Duration;

public final class FrameworkConstants {

    private FrameworkConstants() {}

    // Timeouts
    public static final Duration EXPLICIT_WAIT = Duration.ofSeconds(15);
    public static final Duration IMPLICIT_WAIT = Duration.ofSeconds(10);
    public static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(30);

    // Default Local Application URLs
    public static final String CRM_URL = "http://localhost:3001";
    public static final String LMS_URL = "http://localhost:3000";
    public static final String SUPABASE_LOCAL_URL = "http://127.0.0.1:54321";

    // Framework Paths & Artifacts
    public static final String EXTENT_REPORT_PATH = "./reports/ExtentReport.html";
    public static final String SCREENSHOTS_DIR = "./screenshots/";
}