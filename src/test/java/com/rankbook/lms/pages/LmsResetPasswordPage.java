package com.rankbook.lms.pages;

import com.rankbook.framework.constants.FrameworkConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LmsResetPasswordPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Resilient landing page locators for Next.js student portal
    private final By mainContentArea = By.cssSelector("main, [role='main'], div[data-testid='dashboard-content']");
    private final By headerElements = By.xpath("//h1 | //h2 | //h3 | //header");
    private final By navigationElements = By.cssSelector("nav, aside, [data-testid='sidebar']");

    public LmsResetPasswordPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, FrameworkConstants.EXPLICIT_WAIT);
    }

    public boolean isDashboardLoaded() {
        try {
            // 1. Confirm the page navigated away from the login form/root
            wait.until(driver -> !driver.getCurrentUrl().endsWith(":3000/") 
                              && !driver.getCurrentUrl().contains("/sign-in") 
                              && !driver.getCurrentUrl().contains("/login"));

            // 2. Wait for either the main content container or standard navigation
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(mainContentArea),
                ExpectedConditions.visibilityOfElementLocated(headerElements),
                ExpectedConditions.visibilityOfElementLocated(navigationElements)
            ));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getCurrentPageUrl() {
        return this.driver.getCurrentUrl();
    }
}