package com.rankbook.e2e.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.driver.PlaywrightFactory;
import com.rankbook.framework.utils.CredentialGenerator;
import com.rankbook.framework.utils.SupabaseUserSeeder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FastLeadSeedingToLmsPlaywrightTest {

    private Page page;

    @BeforeMethod
    public void setUp() {
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        page = PlaywrightFactory.initPlaywright("chromium", headless);
    }

    @Test(description = "Verify high-speed LMS authentication & dashboard verification via Playwright")
    public void testFastStudentLoginWithPlaywright() {
        // 1. Generate realistic test student data
        long timestamp = System.currentTimeMillis();
        String firstName = "Student";
        String middleDigits = String.valueOf(timestamp).substring(8);
        String phone = "9" + middleDigits + "3210";
        String email = "pw.student." + timestamp + "@rankbook.test";
        String password = CredentialGenerator.generateStudentPassword(firstName, phone);

        // 2. Provision the student account directly in Supabase
        SupabaseUserSeeder.createOrConfirmStudentUser(email, password);

        // 3. Resolve LMS URL and navigate
        String lmsUrl = ConfigManager.get("lms.url");
        if (lmsUrl == null || lmsUrl.isBlank()) {
            lmsUrl = "http://localhost:3000";
        }
        page.navigate(lmsUrl);

        // 4. Fill credentials & authenticate
        page.fill("input[type='email'], input[name='email']", email);
        page.fill("input[type='password'], input[name='password']", password);
        page.click("button[type='submit']");

        // 5. Assert URL transitions to dashboard or main content loads
        page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(15000));
        PlaywrightAssertions.assertThat(page.locator("body")).isVisible();
    }

    @AfterMethod
    public void tearDown() {
        PlaywrightFactory.quitPlaywright();
    }
}