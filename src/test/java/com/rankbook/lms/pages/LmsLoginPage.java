package com.rankbook.lms.pages;

import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.constants.FrameworkConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LmsLoginPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    // Locators matched to your LMS screenshot
    private final By emailInput = By.cssSelector("input[type='email'], input[placeholder*='@rankbook.in'], input[name='email']");
    private final By passwordInput = By.cssSelector("input[type='password'], input[name='password']");
    private final By submitBtn = By.xpath("//button[contains(., 'Sign In') or @type='submit']");

    public LmsLoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, FrameworkConstants.EXPLICIT_WAIT);
    }

    public void navigateToLogin() {
        // Navigates directly to LMS root (http://localhost:3000)
        driver.get(ConfigManager.get("lms.web.url"));
    }

    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput)).clear();
        driver.findElement(emailInput).sendKeys(email);
        driver.findElement(passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(password);
        driver.findElement(submitBtn).click();
    }
}