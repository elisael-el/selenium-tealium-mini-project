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

public class Test04_SaleProducts extends BaseTest {

    @Test
    void saleProductsTest() {
        System.out.println("=== TEST 04: SALE PRODUCTS STYLE CHECK STARTED ===");

        HomePage home = new HomePage(driver);
        LoginPage login = new LoginPage(driver);
        SalePage sale = new SalePage(driver);

        // Precondition: Sign In
        System.out.println("Step 1: Logging in...");
        home.clickLogin();
        login.login(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        // Step 2: Navigate to Sale section
        System.out.println("Step 2: Navigating to Sale section...");
        home.goToSale();

        // Step 3: Get old and special prices
        List<WebElement> oldPrices = sale.getOldPrices();
        List<WebElement> specialPrices = sale.getSpecialPrices();

        System.out.println("Found " + oldPrices.size() + " old prices");
        System.out.println("Found " + specialPrices.size() + " special prices");

        // Verify at least one product has both prices
        assertTrue(oldPrices.size() > 0 && specialPrices.size() > 0,
                "Sale products should have both original and discounted prices");

        // Check first old price
        WebElement oldPrice = oldPrices.get(0);
        boolean isStrikethrough = sale.isOldPriceStrikethrough(oldPrice);
        assertTrue(isStrikethrough, "Original price should have strikethrough");

        boolean isGrey = sale.isOldPriceGrey(oldPrice);
        assertTrue(isGrey, "Original price should be grey");

        // Check first special price
        WebElement specialPrice = specialPrices.get(0);
        boolean notStrikethrough = sale.isSpecialPriceNotStrikethrough(specialPrice);
        assertTrue(notStrikethrough, "Special price should NOT have strikethrough");

        boolean isBlue = sale.isSpecialPriceBlue(specialPrice);
        assertTrue(isBlue, "Special price should be blue");

        System.out.println("=== TEST 04: SALE PRODUCTS STYLE CHECK PASSED ===");
    }
}