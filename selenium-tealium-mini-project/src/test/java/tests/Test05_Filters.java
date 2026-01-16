package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import pages.FiltersPage;
import pages.HomePage;
import pages.LoginPage;
import utils.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test05_Filters extends BaseTest {

    @Test
    void testFilters() {
        System.out.println("=== Test 5: Check Page Filters ===");

        try {
            // Precondition: Login
            System.out.println("\n--- Precondition: Logging in ---");
            loginToApplication();
            System.out.println("✓ Login successful");

            // Step 1: Navigate to Men section
            System.out.println("\n--- Step 1: Navigate to Men section ---");
            HomePage homePage = new HomePage(driver);
            homePage.navigateToMenSection();
            System.out.println("✓ Navigated to Men page");

            FiltersPage filtersPage = new FiltersPage(driver);

            // Step 2: Click on Black color FIRST (before price filter)
            System.out.println("\n--- Step 2: Select Black color filter ---");
            filtersPage.selectColorFilter("Black");
            System.out.println("✓ Black color filter applied");

            // Step 3: Verify products are filtered
            System.out.println("\n--- Step 3: Verify products are filtered by Black color ---");
            List<WebElement> productsAfterColorFilter = filtersPage.getFilteredProducts();

            assertTrue(productsAfterColorFilter.size() > 0,
                    "Should have products after applying black color filter");

            System.out.println("✓ Found " + productsAfterColorFilter.size() + " products after color filter");
            System.out.println("✓ Color filter successfully applied");

            // Step 4: NOW navigate to Men page AGAIN (without color filter) for price test
            System.out.println("\n--- Step 4: Navigate to Men page again for Price filter test ---");
            driver.get(TestData.BASE_URL + "men.html");

            // Wait for page to load
            wait.until(driver ->
                    js.executeScript("return document.readyState").equals("complete")
            );

            System.out.println("✓ Refreshed Men page");

            // Step 5: Select Price range $0.00 - $99.99 (without color filter)
            System.out.println("\n--- Step 5: Select Price range $0.00 - $99.99 ---");
            filtersPage.selectPriceRange("$0.00 - $99.99");
            System.out.println("✓ Price range filter applied");

            // Step 6: Check that only 3 products are displayed
            System.out.println("\n--- Step 6: Verify 3 products are displayed ---");
            List<WebElement> productsAfterPriceFilter = filtersPage.getFilteredProducts();

            int productCount = productsAfterPriceFilter.size();
            System.out.println("Products after price filter: " + productCount);

            assertEquals(3, productCount,
                    "Should have exactly 3 products after applying price filter $0.00 - $99.99");

            System.out.println("✓ Exactly 3 products displayed");

            // Step 7: Verify each product price is within range
            System.out.println("\n--- Step 7: Verify each product price matches criteria ---");

            double minPrice = 0.00;
            double maxPrice = 99.99;
            int productsInRange = 0;

            for (int i = 0; i < productsAfterPriceFilter.size(); i++) {
                WebElement product = productsAfterPriceFilter.get(i);
                String productName = filtersPage.getProductName(product);

                System.out.println("\n  Checking Product " + (i + 1) + ": " + productName);

                double price = filtersPage.getProductPrice(product);

                if (price > 0) {
                    boolean inRange = filtersPage.isPriceInRange(price, minPrice, maxPrice);

                    assertTrue(inRange,
                            "Product '" + productName + "' price $" + price +
                                    " should be within range $" + minPrice + " - $" + maxPrice);

                    productsInRange++;
                }
            }

            assertEquals(3, productsInRange,
                    "All 3 products should have prices within the specified range");

            System.out.println("\n✓ All product prices verified");

            // Final Summary
            System.out.println("\n=== FINAL SUMMARY ===");
            System.out.println("✓ Navigated to Men page");
            System.out.println("✓ Applied Black color filter");
            System.out.println("✓ Applied Price filter ($0.00 - $99.99)");
            System.out.println("✓ Verified exactly 3 products displayed");
            System.out.println("✓ Verified all 3 products have prices in range");

            System.out.println("\n✓✓✓ Test 5 PASSED SUCCESSFULLY ✓✓✓");

        } catch (AssertionError e) {
            System.err.println("\n✗✗✗ Test 5 FAILED ✗✗✗");
            System.err.println("Assertion Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n✗✗✗ Test 5 FAILED with exception ✗✗✗");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    private void loginToApplication() {
        HomePage homePage = new HomePage(driver);
        homePage.clickLogin();

        LoginPage loginPage = new LoginPage(driver);
        String email = TestData.getEmailForLogin();
        String password = TestData.getPasswordForLogin();

        System.out.println("Using email: " + email);
        loginPage.login(email, password);

        assertTrue(loginPage.isUserLoggedIn(), "User should be logged in");
    }
}