package com.rankbook.crm.tests;

import com.rankbook.base.BaseSeleniumTest;
import com.rankbook.crm.models.LeadCreateRequest;
import com.rankbook.crm.pages.CrmKanbanPage;
import com.rankbook.crm.pages.CrmLoginPage;
import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.listeners.TestListener;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class CrmLeadManagementTest extends BaseSeleniumTest {

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

    @Test(priority = 1, description = "Verify admin can create a new lead via CRM modal")
    public void testCreateNewLead() {
        LeadCreateRequest testLead = LeadCreateRequest.createDefaultUnique("Rohan", "Kapoor");
        kanbanPage.createLead(testLead.getName(), testLead.getPhone(), testLead.getEmail());

        Assert.assertTrue(kanbanPage.isLoaded(), "Pipeline view was disrupted after creating lead!");
        Assert.assertTrue(kanbanPage.isLeadPresent(testLead.getName()), "Created lead is not visible on pipeline board!");
    }

    @Test(priority = 2, description = "Verify CRM blocks duplicate lead creation by phone or email")
    public void testDuplicateLeadRejection() {
        LeadCreateRequest originalLead = LeadCreateRequest.createDefaultUnique("Vikram", "Malhotra");
        kanbanPage.createLead(originalLead.getName(), originalLead.getPhone(), originalLead.getEmail());

        kanbanPage.submitLeadExpectingRejection("Duplicate Lead", originalLead.getPhone(), originalLead.getEmail());

        boolean modalRemains = kanbanPage.isModalStillOpen();
        String errorMsg = kanbanPage.getRejectionMessage();
        kanbanPage.closeModalIfOpen();

        Assert.assertTrue(modalRemains || !errorMsg.isEmpty(), 
                "CRM unexpectedly allowed duplicate lead submission without rejection!");
    }

    @Test(priority = 3, description = "Verify lead stage transition on the Kanban pipeline board")
    public void testLeadStageTransition() {
        LeadCreateRequest lead = LeadCreateRequest.createDefaultUnique("Arjun", "Reddy");
        kanbanPage.createLead(lead.getName(), lead.getPhone(), lead.getEmail());

        Assert.assertTrue(kanbanPage.isLeadPresent(lead.getName()), "Initial lead was not found on board!");

        // Move across board
        kanbanPage.moveLeadToColumn(lead.getName(), "Attempted Contact");
        Assert.assertTrue(kanbanPage.isLoaded(), "Board failed to maintain state after moving lead!");
    }
}