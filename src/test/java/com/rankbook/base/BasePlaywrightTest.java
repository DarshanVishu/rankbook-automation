package com.rankbook.base;

import com.microsoft.playwright.Page;
import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.driver.PlaywrightFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BasePlaywrightTest {
    protected Page page;

    @BeforeMethod
    public void setUp() {
        String browser = ConfigManager.get("browser");
        boolean headless = Boolean.parseBoolean(ConfigManager.get("headless"));
        page = PlaywrightFactory.initPlaywright(browser, headless);
    }

    @AfterMethod
    public void tearDown() {
        PlaywrightFactory.quitPlaywright();
    }
}