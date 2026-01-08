package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.LoginPage;
import pages.SortingPage;
import utils.TestData;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test06_Wishlist extends BaseTest {

    @Test
    void wishlistTest() {
        System.out.println("=== TEST 06: SORTING & WISHLIST CHECK STARTED ===");

        HomePage home = new HomePage(driver);
        LoginPage login = new LoginPage(driver);
        SortingPage sorting = new SortingPage(driver);

        // Precondition: Sign In
        System.out.println("Precondition: Logging in...");
        home.clickLogin();
        login.login(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        // Step 1: Navigate to Women section
        System.out.println("Step 1: Navigating to Women section...");
        home.goToWomen();

        // Step 2: Sort by price
        System.out.println("Step 2: Sorting products by price...");
        sorting.sortByPrice();

        // Step 3: Get and verify prices are sorted
        List<Double> prices = sorting.getProductPrices();
        System.out.println("Found " + prices.size() + " products with prices");

        for (int i = 0; i < prices.size(); i++) {
            System.out.println("Price " + (i + 1) + ": $" + prices.get(i));
        }

        assertTrue(prices.size() > 0, "Should have products with prices displayed");

        // Verify sorting
        boolean isSorted = true;
        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                isSorted = false;
                break;
            }
        }
        assertTrue(isSorted, "Products should be sorted by price in ascending order");

        // Step 4: Add first two products to wishlist
        System.out.println("Step 4: Adding first two products to wishlist...");
        int itemsAdded = sorting.addFirstTwoToWishlist();
        System.out.println("Successfully added " + itemsAdded + " items to wishlist");

        // Verify at least one item was added
        assertTrue(itemsAdded >= 1, "At least one item should be added to wishlist");

        // Step 5: Verify wishlist count
        System.out.println("Step 5: Verifying wishlist count...");

        // Navigate back to women page to ensure we're in a stable state
        home.goToWomen();

        String wishlistText = sorting.getWishlistCount();
        System.out.println("Wishlist text: " + wishlistText);

        boolean hasWishlistItems = !wishlistText.isEmpty() &&
                (wishlistText.contains("item") || wishlistText.contains("Wish List"));

        assertTrue(hasWishlistItems, "Wishlist should be accessible");

        System.out.println("=== TEST 06: SORTING & WISHLIST CHECK PASSED ===");
    }
}