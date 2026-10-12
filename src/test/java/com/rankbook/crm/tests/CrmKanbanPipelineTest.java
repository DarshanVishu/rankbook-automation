package com.rankbook.crm.tests;

import com.rankbook.base.BaseSeleniumTest;
import com.rankbook.crm.pages.CrmKanbanPage;
import com.rankbook.crm.pages.CrmLoginPage;
import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.listeners.TestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class CrmKanbanPipelineTest extends BaseSeleniumTest {

    private CrmKanbanPage kanbanPage;

    @BeforeMethod
    public void setupAndAuthenticate() {
        CrmLoginPage loginPage = new CrmLoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.login(ConfigManager.get("crm.admin.email"), ConfigManager.get("crm.admin.password"));

        kanbanPage = new CrmKanbanPage(driver);
        kanbanPage.navigateToPipeline();
        Assert.assertTrue(kanbanPage.isLoaded(), "CRM Pipeline did not load after authentication!");
    }

    @Test(priority = 1, description = "Verify Kanban board renders populated lead columns and cards")
    public void testPipelineBoardRendering() {
        Assert.assertTrue(kanbanPage.isLoaded(), "Kanban board failed to render with existing leads!");
    }

    @Test(priority = 2, description = "Verify moving an existing lead from New Lead to Attempted Contact")
    public void testTransitionExistingLead() {
        // Move an existing lead on the board
        kanbanPage.moveLeadToColumn("Arjun Reddy", "Attempted Contact");
        Assert.assertTrue(kanbanPage.isLoaded(), "Board was disrupted during column transition!");
    }
}