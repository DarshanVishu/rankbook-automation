package com.rankbook.lms.tests;

import com.rankbook.base.BaseSeleniumTest;
import com.rankbook.framework.listeners.TestListener;
import com.rankbook.framework.utils.CredentialGenerator;
import com.rankbook.framework.utils.SupabaseUserSeeder;
import com.rankbook.lms.pages.LmsLoginPage;
import com.rankbook.lms.pages.LmsStudentDashboardPage;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class LmsStudentLoginTest extends BaseSeleniumTest {

    @Test(priority = 1, description = "Verify successful student login with valid credentials")
    public void testValidStudentLogin() {
        long timestamp = System.currentTimeMillis();
        String firstName = "Student";
        String middleDigits = String.valueOf(timestamp).substring(8);
        String phone = "9" + middleDigits + "3210";
        String email = "student." + timestamp + "@rankbook.test";

        // Generate deterministic password (e.g., Student@3210)
        String password = CredentialGenerator.generateStudentPassword(firstName, phone);

        // Ensure user is provisioned in local Supabase
        SupabaseUserSeeder.createOrConfirmStudentUser(email, password);

        // Execute login flow on LMS (Port 3000)
        LmsLoginPage loginPage = new LmsLoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.login(email, password);

        // Validate dashboard landing
        LmsStudentDashboardPage dashboardPage = new LmsStudentDashboardPage(driver);
        Assert.assertTrue(dashboardPage.isDashboardLoaded(), 
                "Student dashboard failed to load after valid login credentials!");
    }

    @Test(priority = 2, description = "Verify LMS rejection when using an incorrect password")
    public void testInvalidPasswordRejection() {
        LmsLoginPage loginPage = new LmsLoginPage(driver);
        loginPage.navigateToLogin();
        
        loginPage.login("student.existing@rankbook.test", "WrongPassword@999");

        // Verify the browser remains on login and is not routed to dashboard
        Assert.assertFalse(driver.getCurrentUrl().contains("dashboard"), 
                "Unauthorized user was improperly granted access to dashboard!");
    }

    @Test(priority = 3, description = "Verify LMS rejection for non-existent student user")
    public void testNonExistentUserRejection() {
        LmsLoginPage loginPage = new LmsLoginPage(driver);
        loginPage.navigateToLogin();

        loginPage.login("nonexistent." + System.currentTimeMillis() + "@rankbook.test", "SomePass@123");

        Assert.assertFalse(driver.getCurrentUrl().contains("dashboard"), 
                "Non-existent student was improperly redirected to dashboard!");
    }
}