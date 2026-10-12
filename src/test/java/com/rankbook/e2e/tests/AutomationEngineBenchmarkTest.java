package com.rankbook.e2e.tests;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.utils.CredentialGenerator;
import com.rankbook.framework.utils.SupabaseUserSeeder;
import com.rankbook.lms.pages.LmsLoginPage;
import com.rankbook.lms.pages.LmsStudentDashboardPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AutomationEngineBenchmarkTest {

    @Test(description = "Benchmark side-by-side execution time: Selenium vs Playwright")
    public void benchmarkSeleniumVsPlaywright() {
        long timestamp = System.currentTimeMillis();
        String email = "bench." + timestamp + "@rankbook.test";
        String password = CredentialGenerator.generateStudentPassword("Bench", "9876543210");
        SupabaseUserSeeder.createOrConfirmStudentUser(email, password);

        String lmsUrl = ConfigManager.get("lms.url");
        if (lmsUrl == null || lmsUrl.isBlank()) {
            lmsUrl = "http://localhost:3000";
        }

        System.out.println("=================================================");
        System.out.println("  STARTING PERFORMANCE BENCHMARK (HEADLESS)     ");
        System.out.println("=================================================");

        // --- 1. BENCHMARK SELENIUM ---
        long startSelenium = System.currentTimeMillis();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080");
        WebDriver driver = new ChromeDriver(options);

        try {
            LmsLoginPage loginPage = new LmsLoginPage(driver);
            loginPage.navigateToLogin();
            loginPage.login(email, password);

            LmsStudentDashboardPage dashboard = new LmsStudentDashboardPage(driver);
            Assert.assertTrue(dashboard.isDashboardLoaded(), "Selenium dashboard failed to load");
        } finally {
            driver.quit();
        }

        long seleniumDurationMs = System.currentTimeMillis() - startSelenium;

        // --- 2. BENCHMARK PLAYWRIGHT ---
        long startPlaywright = System.currentTimeMillis();

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
            Page page = browser.newPage();
            page.navigate(lmsUrl);
            page.fill("input[type='email'], input[name='email']", email);
            page.fill("input[type='password'], input[name='password']", password);
            page.click("button[type='submit']");
            page.waitForURL("**/dashboard**", new Page.WaitForURLOptions().setTimeout(15000));
            Assert.assertTrue(page.locator("body").isVisible());
            browser.close();
        }

        long playwrightDurationMs = System.currentTimeMillis() - startPlaywright;

        // --- LOG COMPARISON METRICS ---
        System.out.println("-------------------------------------------------");
        System.out.println("  BENCHMARK RESULTS SUMMARY                      ");
        System.out.println("-------------------------------------------------");
        System.out.printf("  Selenium (Headless Chrome):  %,d ms%n", seleniumDurationMs);
        System.out.printf("  Playwright (Chromium Engine): %,d ms%n", playwrightDurationMs);
        double ratio = (double) seleniumDurationMs / (playwrightDurationMs > 0 ? playwrightDurationMs : 1);
        System.out.printf("  Speed Factor: Playwright was %.2fx faster%n", ratio);
        System.out.println("=================================================");
    }
}