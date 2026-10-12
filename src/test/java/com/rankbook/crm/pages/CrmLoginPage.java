package com.rankbook.crm.pages;

import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.constants.FrameworkConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CrmLoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Resilient input locators
    private final By emailInput = By.cssSelector("input[type='email'], input[name='email']");
    private final By passwordInput = By.cssSelector("input[type='password'], input[name='password']");
    private final By submitBtn = By.cssSelector("button[type='submit']");

    public CrmLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, FrameworkConstants.EXPLICIT_WAIT);
    }

    public void navigateToLogin() {
        // Navigate to base URL; if your app uses /sign-in, change to: ConfigManager.get("crm.web.url") + "/sign-in"
        driver.get(ConfigManager.get("crm.web.url"));
    }

    public void login(String email, String password) {
        // If already logged in and on /dashboard, proceed
        if (driver.getCurrentUrl().contains("/dashboard") || driver.getCurrentUrl().contains("/pipeline")) {
            return;
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput)).clear();
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(submitBtn).click();

        // Wait until navigation transitions to dashboard or pipeline
        wait.until(ExpectedConditions.or(
            ExpectedConditions.urlContains("/dashboard"),
            ExpectedConditions.urlContains("/pipeline")
        ));
    }
}