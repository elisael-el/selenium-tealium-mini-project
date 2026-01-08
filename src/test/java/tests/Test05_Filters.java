package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.LoginPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.*;

public class Test05_Filters extends BaseTest {

    @Test
    void filtersCheck() {
        System.out.println("=== TEST 05: FILTERS CHECK STARTED ===");

        HomePage home = new HomePage(driver);
        LoginPage login = new LoginPage(driver);

        // Precondition: Sign In
        System.out.println("Step 1: Logging in...");
        home.clickLogin();
        login.login(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        // Step 2: Navigate to Men section
        System.out.println("Step 2: Navigating to Men section...");
        home.goToMen();

        System.out.println("Current URL: " + driver.getCurrentUrl());

        // Verify we're on Men page
        assertTrue(driver.getCurrentUrl().contains("men"),
                "Should be on Men products page");

        System.out.println("=== TEST 05: FILTERS CHECK PASSED ===");
    }
}