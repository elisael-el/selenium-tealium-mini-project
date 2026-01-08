package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.LoginPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.*;

public class Test07_ShoppingCart extends BaseTest {

    @Test
    void shoppingCartTest() {
        System.out.println("=== TEST 07: SHOPPING CART TEST STARTED ===");

        HomePage home = new HomePage(driver);
        LoginPage login = new LoginPage(driver);

        // Precondition: Sign In
        System.out.println("Step 1: Logging in...");
        home.clickLogin();
        login.login(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        // Step 2: Navigate to cart
        System.out.println("Step 2: Navigating to shopping cart...");
        driver.get(TestData.BASE_URL + "checkout/cart/");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Current URL: " + driver.getCurrentUrl());

        // Verify we're on cart page
        assertTrue(driver.getCurrentUrl().contains("cart") ||
                        driver.getCurrentUrl().contains("checkout"),
                "Should be on shopping cart page");

        System.out.println("=== TEST 07: SHOPPING CART TEST PASSED ===");
    }
}