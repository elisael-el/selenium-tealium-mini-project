package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import pages.HomePage;
import pages.LoginPage;
import pages.SalePage;
import utils.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test 4: Check sale products style
 * Precondition: Sign In Tealium Application
 *
 * 1. Hover over Sale and click View All Sale
 * 2. For each product, check if multiple prices are shown (original + discounted)
 * 3. Verify that the original price has grey color and is strikethrough
 * 4. Verify that the final price does not have a strikethrough and has blue color
 */
public class Test04_SaleProducts extends BaseTest {

    @Test
    void testSaleProductsStyle() {
        System.out.println("=== Test 4: Check Sale Products Style ===");

        try {
            // Precondition: Login
            System.out.println("\n--- Precondition: Logging in ---");
            loginToApplication();
            System.out.println("✓ Login successful");

            // Step 1: Navigate to Sale page
            System.out.println("\n--- Step 1: Navigate to Sale section ---");
            HomePage homePage = new HomePage(driver);

            System.out.println("Current URL before navigation: " + driver.getCurrentUrl());
            homePage.navigateToSaleSection();
            System.out.println("Current URL after navigation: " + driver.getCurrentUrl());
            System.out.println("✓ Navigated to Sale page");

            // Step 2-4: Check each product for correct pricing display
            System.out.println("\n--- Step 2-4: Verify sale products styling ---");
            SalePage salePage = new SalePage(driver);

            List<WebElement> saleProducts = salePage.getSaleProducts();
            assertTrue(saleProducts.size() > 0, "Sale page should have at least one product");
            System.out.println("Total products found: " + saleProducts.size());

            // Counters for summary
            int totalProducts = saleProducts.size();
            int productsWithMultiplePrices = 0;
            int productsFullyVerified = 0;
            int oldPriceCorrect = 0;
            int specialPriceCorrect = 0;

            // Check each product
            for (int i = 0; i < saleProducts.size(); i++) {
                System.out.println("\n  === Checking Product " + (i + 1) + " of " + totalProducts + " ===");

                WebElement product = saleProducts.get(i);

                // Get product name for better logging
                String productName = salePage.getProductName(product);
                System.out.println("  Product: " + productName);

                // Step 2: Check if product has multiple prices (original + discounted)
                boolean hasMultiplePrices = salePage.hasMultiplePrices(product);

                if (!hasMultiplePrices) {
                    System.out.println("  ⚠ Product " + (i + 1) + " does not have both prices - skipping");
                    continue;
                }

                productsWithMultiplePrices++;

                // Get price elements
                WebElement oldPrice = salePage.getOldPrice(product);
                WebElement specialPrice = salePage.getSpecialPrice(product);

                if (oldPrice == null || specialPrice == null) {
                    System.out.println("  ⚠ Could not get price elements - skipping");
                    continue;
                }

                System.out.println("  Old price text: " + oldPrice.getText());
                System.out.println("  Special price text: " + specialPrice.getText());

                // Step 3: Verify OLD PRICE styling (grey + strikethrough)
                System.out.println("\n  --- Verifying OLD PRICE ---");

                boolean isOldPriceStrikethrough = salePage.isOldPriceStrikethrough(oldPrice);
                boolean isOldPriceGrey = salePage.isOldPriceGrey(oldPrice);

                // Assert old price styling
                assertTrue(isOldPriceStrikethrough,
                        "Product " + (i + 1) + " (" + productName + "): Old price should be strikethrough");
                assertTrue(isOldPriceGrey,
                        "Product " + (i + 1) + " (" + productName + "): Old price should be grey");

                oldPriceCorrect++;
                System.out.println("  ✓ Old price styling verified (strikethrough + grey)");

                // Step 4: Verify SPECIAL PRICE styling (NO strikethrough + blue)
                System.out.println("\n  --- Verifying SPECIAL PRICE ---");

                boolean isSpecialPriceNotStrikethrough = salePage.isSpecialPriceNotStrikethrough(specialPrice);
                boolean isSpecialPriceBlue = salePage.isSpecialPriceBlue(specialPrice);

                // Assert special price styling
                assertTrue(isSpecialPriceNotStrikethrough,
                        "Product " + (i + 1) + " (" + productName + "): Special price should NOT be strikethrough");
                assertTrue(isSpecialPriceBlue,
                        "Product " + (i + 1) + " (" + productName + "): Special price should be blue");

                specialPriceCorrect++;
                System.out.println("  ✓ Special price styling verified (no strikethrough + blue)");

                productsFullyVerified++;
                System.out.println("\n  ✓✓✓ Product " + (i + 1) + " fully verified ✓✓✓");
            }

            // Final Summary
            System.out.println("\n=== FINAL SUMMARY ===");
            System.out.println("Total products on page: " + totalProducts);
            System.out.println("Products with multiple prices: " + productsWithMultiplePrices);
            System.out.println("Products with correct old price: " + oldPriceCorrect);
            System.out.println("Products with correct special price: " + specialPriceCorrect);
            System.out.println("Products fully verified: " + productsFullyVerified);

            // Final Assertions
            assertTrue(productsWithMultiplePrices > 0,
                    "At least one product should have multiple prices (original + discounted)");
            assertTrue(productsFullyVerified > 0,
                    "At least one product should be fully verified with correct styling");

            System.out.println("\n✓✓✓ Test 4 PASSED SUCCESSFULLY ✓✓✓");
            System.out.println("All sale products have correct pricing display!");

        } catch (AssertionError e) {
            System.err.println("\n✗✗✗ Test 4 FAILED ✗✗✗");
            System.err.println("Assertion Error: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("\n✗✗✗ Test 4 FAILED with exception ✗✗✗");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            fail("Test failed with exception: " + e.getMessage());
        }
    }

    /**
     * Helper method to login to the application
     * Uses credentials from TestData
     */
    private void loginToApplication() {
        HomePage homePage = new HomePage(driver);
        homePage.clickLogin();

        LoginPage loginPage = new LoginPage(driver);
        String email = TestData.getEmailForLogin();
        String password = TestData.getPasswordForLogin();

        System.out.println("Using email: " + email);
        loginPage.login(email, password);

        // Verify login was successful
        assertTrue(loginPage.isUserLoggedIn(), "User should be logged in after providing credentials");
    }
}