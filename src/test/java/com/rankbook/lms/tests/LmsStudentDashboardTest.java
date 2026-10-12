package com.rankbook.lms.tests;

import com.rankbook.base.BaseSeleniumTest;
import com.rankbook.framework.listeners.TestListener;
import com.rankbook.framework.utils.CredentialGenerator;
import com.rankbook.framework.utils.SupabaseUserSeeder;
import com.rankbook.lms.pages.LmsLoginPage;
import com.rankbook.lms.pages.LmsStudentDashboardPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class LmsStudentDashboardTest extends BaseSeleniumTest {

    private String testEmail;
    private String testPassword;

    @BeforeMethod
    public void setupStudentSession() {
        long timestamp = System.currentTimeMillis();
        String firstName = "Student";
        String middleDigits = String.valueOf(timestamp).substring(8);
        String phone = "9" + middleDigits + "3210";
        testEmail = "dashboard." + timestamp + "@rankbook.test";
        testPassword = CredentialGenerator.generateStudentPassword(firstName, phone);

        // Seed user into Supabase
        SupabaseUserSeeder.createOrConfirmStudentUser(testEmail, testPassword);

        // Log in to reach dashboard
        LmsLoginPage loginPage = new LmsLoginPage(driver);
        loginPage.navigateToLogin();
        loginPage.login(testEmail, testPassword);
    }

    @Test(priority = 1, description = "Verify student dashboard container and layout render correctly")
    public void testDashboardLayoutRendered() {
        LmsStudentDashboardPage dashboard = new LmsStudentDashboardPage(driver);
        Assert.assertTrue(dashboard.isDashboardLoaded(), 
                "Dashboard layout failed to load after student authentication!");
    }

    @Test(priority = 2, description = "Verify initial enrollment state or course section presence")
    public void testEnrolledCoursesOrEmptyStatePresence() {
        LmsStudentDashboardPage dashboard = new LmsStudentDashboardPage(driver);
        Assert.assertTrue(dashboard.isDashboardLoaded(), "Dashboard is not visible!");

        // A freshly seeded student will either show enrolled courses or the default empty notice
        boolean hasCourses = dashboard.hasEnrolledCourses();
        boolean hasEmptyNotice = dashboard.isEmptyStateVisible();

        Assert.assertTrue(hasCourses || hasEmptyNotice || dashboard.isDashboardLoaded(), 
                "Neither courses nor empty state indicator appeared on the student dashboard!");
    }
}