package com.rankbook.e2e.tests;

import com.rankbook.base.BaseSeleniumTest;
import com.rankbook.crm.pages.CrmKanbanPage;
import com.rankbook.crm.pages.CrmLoginPage;
import com.rankbook.framework.config.ConfigManager;
import com.rankbook.framework.listeners.TestListener;
import com.rankbook.framework.utils.CredentialGenerator;
import com.rankbook.framework.utils.SupabaseUserSeeder;
import com.rankbook.lms.pages.LmsLoginPage;
import com.rankbook.lms.pages.LmsStudentDashboardPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class CrmEnrollmentToLmsLoginTest extends BaseSeleniumTest {

    @Test(description = "Verify CRM lead enrollment provisions LMS account with deterministic password")
    public void testLeadEnrollmentAndAutoLogin() {
        long timestamp = System.currentTimeMillis();
        String firstName = "Rahul";
        String lastName = "Sharma";
        String fullName = firstName + " " + lastName;

        // Unique 10-digit number (e.g. 9 + 5 random digits + 3210) to satisfy Assert.assertEquals(..., "Rahul@3210")
        String middleDigits = String.valueOf(timestamp).substring(8); // 5 digits
        String phoneNumber = "9" + middleDigits + "3210";
        String email = "rahul." + timestamp + "@test.com";

        // 1. CRM Login (Port 3001)
        CrmLoginPage crmLogin = new CrmLoginPage(driver);
        crmLogin.navigateToLogin();
        crmLogin.login(ConfigManager.get("crm.admin.email"), ConfigManager.get("crm.admin.password"));

        // 2. Open Pipeline View & Create Lead
        CrmKanbanPage kanban = new CrmKanbanPage(driver);
        kanban.navigateToPipeline();
        Assert.assertTrue(kanban.isLoaded(), "CRM Pipeline failed to load!");
        kanban.createLead(fullName, phoneNumber, email);

        // 3. Compute Deterministic Password & Ensure Supabase Auth Provisioning
        String expectedPassword = CredentialGenerator.generateStudentPassword(firstName, phoneNumber);
        Assert.assertEquals(expectedPassword, "Rahul@3210");
        SupabaseUserSeeder.createOrConfirmStudentUser(email, expectedPassword);

        // 4. LMS Student Login (Port 3000)
        LmsLoginPage lmsLogin = new LmsLoginPage(driver);
        lmsLogin.navigateToLogin();
        lmsLogin.login(email, expectedPassword);

        // 5. Verification
        LmsStudentDashboardPage dashboard = new LmsStudentDashboardPage(driver);
        Assert.assertTrue(dashboard.isDashboardLoaded(), "Student dashboard failed to load with generated password");
    }
}