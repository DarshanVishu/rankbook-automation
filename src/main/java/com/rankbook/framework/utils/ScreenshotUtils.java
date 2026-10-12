package com.rankbook.framework.utils;

import com.microsoft.playwright.Page;
import com.rankbook.framework.driver.DriverFactory;
import com.rankbook.framework.driver.PlaywrightFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.Base64;

public class ScreenshotUtils {

    /**
     * Captures a raw Base64 screenshot from either the active Selenium WebDriver
     * or active Playwright Page instance.
     * 
     * @return raw Base64 encoded PNG string, or null if neither driver is active
     */
    public static String captureBase64Screenshot() {
        // 1. Try Selenium WebDriver first
        try {
            WebDriver seleniumDriver = DriverFactory.getDriver();
            if (seleniumDriver instanceof TakesScreenshot takesScreenshot) {
                // Ensure session is alive by checking window handle
                if (!seleniumDriver.getWindowHandles().isEmpty()) {
                    return takesScreenshot.getScreenshotAs(OutputType.BASE64);
                }
            }
        } catch (Exception e) {
            System.err.println("[ScreenshotUtils] Selenium capture skipped or failed: " + e.getMessage());
        }

        // 2. Try Playwright Page fallback if Selenium is inactive or failed
        try {
            Page playwrightPage = PlaywrightFactory.getPage();
            if (playwrightPage != null && !playwrightPage.isClosed()) {
                byte[] buffer = playwrightPage.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                return Base64.getEncoder().encodeToString(buffer);
            }
        } catch (Exception e) {
            System.err.println("[ScreenshotUtils] Playwright capture skipped or failed: " + e.getMessage());
        }

        return null;
    }
}