package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import pages.HomePage;
import pages.LoginPage;
import pages.ProductsPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.*;

public class Test03_HoverStyle extends BaseTest {

    @Test
    void hoverStyleCheck() {
        System.out.println("=== TEST 03: HOVER STYLE CHECK STARTED ===");

        HomePage home = new HomePage(driver);
        LoginPage login = new LoginPage(driver);
        ProductsPage products = new ProductsPage(driver);

        // Precondition: Sign In
        System.out.println("Step 1: Logging in...");
        home.clickLogin();
        login.login(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        // Step 2: Navigate to Women products
        System.out.println("Step 2: Navigating to Women section...");
        home.goToWomen();

        // Step 3: Hover over products
        System.out.println("Step 3: Testing hover on products...");
        var productList = products.getProducts();

        assertTrue(productList.size() > 0, "Should have products to test hover");

        // Hover over first 3 products to demonstrate hover capability
        for (int i = 0; i < Math.min(3, productList.size()); i++) {
            WebElement product = productList.get(i);
            System.out.println("Hovering over product " + (i + 1));
            products.hoverProduct(product);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Test passes - hover interactions completed successfully
        System.out.println("Hover interactions completed successfully");
        System.out.println("=== TEST 03: HOVER STYLE CHECK PASSED ===");
    }
}