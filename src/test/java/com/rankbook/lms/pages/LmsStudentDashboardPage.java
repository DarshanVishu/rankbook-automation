package com.rankbook.lms.pages;

import com.rankbook.framework.constants.FrameworkConstants;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LmsStudentDashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // Resilient landing page locators for Next.js student portal
    private final By mainContentArea = By.cssSelector("main, [role='main'], div[data-testid='dashboard-content']");
    private final By headerElements = By.xpath("//h1 | //h2 | //h3 | //header");
    private final By navigationElements = By.cssSelector("nav, aside, [data-testid='sidebar']");

    // Course and status locators
    private final By courseCards = By.xpath(
            "//div[contains(@class,'course-card') or contains(@data-testid,'course-card')] | //div[contains(@class,'card') and .//button[contains(.,'View Course') or contains(.,'Resume')]]"
    );
    private final By emptyStateNotice = By.xpath(
            "//*[contains(text(),'No enrolled courses') or contains(text(),'No courses found') or contains(text(),'No active') or contains(text(),'No enrollments')]"
    );
    private final By logoutButton = By.xpath(
            "//button[contains(.,'Logout') or contains(.,'Sign out')] | //a[contains(.,'Logout') or contains(.,'Sign out')]"
    );

    public LmsStudentDashboardPage(WebDriver driver) {
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

    public int getEnrolledCourseCount() {
        try {
            List<WebElement> cards = driver.findElements(courseCards);
            return cards.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean hasEnrolledCourses() {
        return getEnrolledCourseCount() > 0;
    }

    public boolean isEmptyStateVisible() {
        try {
            return driver.findElement(emptyStateNotice).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public void logout() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(logoutButton)).click();
        } catch (Exception ignored) {
        }
    }

    public String getCurrentPageUrl() {
        return this.driver.getCurrentUrl();
    }
}