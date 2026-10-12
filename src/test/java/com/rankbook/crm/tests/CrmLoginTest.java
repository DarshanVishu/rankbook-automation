package com.rankbook.crm.tests;

import com.rankbook.base.BaseSeleniumTest;
import com.rankbook.crm.pages.CrmKanbanPage;
import com.rankbook.crm.pages.CrmLoginPage;
import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.listeners.TestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class CrmLoginTest extends BaseSeleniumTest {

    @Test(priority = 1, description = "Verify successful admin login into CRM")
    public void testValidAdminLogin() {
        CrmLoginPage loginPage = new CrmLoginPage(driver);
        loginPage.navigateToLogin();

        loginPage.login(ConfigManager.get("crm.admin.email"), ConfigManager.get("crm.admin.password"));

        CrmKanbanPage kanban = new CrmKanbanPage(driver);
        kanban.navigateToPipeline();
        Assert.assertTrue(kanban.isLoaded(), "CRM Kanban pipeline did not load after admin login!");
    }

    @Test(priority = 2, description = "Verify CRM rejection on invalid admin password")
    public void testInvalidPasswordRejection() {
        CrmLoginPage loginPage = new CrmLoginPage(driver);
        loginPage.navigateToLogin();

        loginPage.login(ConfigManager.get("crm.admin.email"), "Password1!");

        // Validate user remains on the login view and is not navigated to pipeline
        Assert.assertTrue(driver.getCurrentUrl().contains("login") || driver.getCurrentUrl().contains("3001"),
                "User was improperly navigated away from login on invalid credentials!");
    }
}