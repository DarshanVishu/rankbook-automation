package com.rankbook.framework.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightFactory {
    private static final ThreadLocal<Playwright> playwrightThread = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThread = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThread = new ThreadLocal<>();

    public static Page initPlaywright(String browserType, boolean headless) {
        Playwright playwright = Playwright.create();
        playwrightThread.set(playwright);

        BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headless);
        Browser browser;

        if ("firefox".equalsIgnoreCase(browserType)) {
            browser = playwright.firefox().launch(options);
        } else if ("webkit".equalsIgnoreCase(browserType)) {
            browser = playwright.webkit().launch(options);
        } else {
            browser = playwright.chromium().launch(options);
        }

        browserThread.set(browser);
        Page page = browser.newPage();
        pageThread.set(page);
        return page;
    }

    public static Page getPage() {
        return pageThread.get();
    }

    public static void quitPlaywright() {
        if (pageThread.get() != null) pageThread.get().close();
        if (browserThread.get() != null) browserThread.get().close();
        if (playwrightThread.get() != null) playwrightThread.get().close();

        pageThread.remove();
        browserThread.remove();
        playwrightThread.remove();
    }
}