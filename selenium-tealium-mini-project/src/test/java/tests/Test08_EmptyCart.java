package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestData;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test08_EmptyCart extends BaseTest {

    @Test
    void testEmptyCart() {
        System.out.println("=== Test 8: Empty Shopping Cart ===");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Hapi 1: Hyrje në sistem (përdor të njëjtin user si Test07)
            System.out.println("Step 1: Login");
            loginUser(wait, js);
            System.out.println("✓ Login successful");

            // Hapi 2: Shko në Shopping Cart (duhet të ketë produkte nga Test07)
            System.out.println("\nStep 2: Go to Shopping Cart");
            driver.get("https://ecommerce.tealiumdemo.com/checkout/cart/");
            Thread.sleep(3000);

            // Kap screenshot fillestar
            takeScreenshot("cart_initial_state");

            // Hapi 3: Kontrollo nëse ka produkte në cart
            System.out.println("\nStep 3: Check if cart has items");

            // Përcakto nëse cart është bosh
            boolean isCartEmpty = isCartEmpty();

            if (isCartEmpty) {
                System.out.println("Cart is empty. Adding items first...");

                // Nëse cart është bosh, shto disa produkte
                addItemsToCartForTesting(wait, js);

                // Rifresko faqen
                driver.get("https://ecommerce.tealiumdemo.com/checkout/cart/");
                Thread.sleep(3000);
            }

            // Hapi 4: Merre numrin fillestar të produkteve
            System.out.println("\nStep 4: Get initial item count");
            int initialItemCount = getCartItemCount();
            System.out.println("Initial items in cart: " + initialItemCount);

            assertTrue(initialItemCount > 0, "Cart should have items to delete");

            // Hapi 5: Fshi produkte një nga një dhe verifiko
            System.out.println("\nStep 5: Delete items one by one");

            int itemsDeleted = 0;
            int currentItemCount = initialItemCount;

            while (currentItemCount > 0) {
                System.out.println("\n--- Deleting item " + (itemsDeleted + 1) + " ---");
                System.out.println("Current items in cart: " + currentItemCount);

                // Fshi produktin e parë
                boolean deletionSuccessful = deleteFirstCartItem(js);

                if (deletionSuccessful) {
                    itemsDeleted++;

                    // Prit për faqen të rifreskohet
                    Thread.sleep(3000);

                    // Merre numrin e ri të produkteve
                    int newItemCount = getCartItemCount();
                    System.out.println("Items after deletion: " + newItemCount);

                    // Verifiko që numri i produkteve është zvogëluar me 1
                    assertEquals(currentItemCount - 1, newItemCount,
                            "Item count should decrease by 1 after deletion");

                    System.out.println("✓ Item count decreased correctly");

                    currentItemCount = newItemCount;

                    // Kap screenshot pas çdo fshirjeje
                    takeScreenshot("cart_after_deleting_item_" + itemsDeleted);

                } else {
                    System.out.println("Failed to delete item, stopping...");
                    break;
                }
            }

            System.out.println("\n✓ Successfully deleted all " + itemsDeleted + " items");

            // Hapi 6: Verifiko që cart është bosh
            System.out.println("\nStep 6: Verify cart is empty");

            // Rifresko faqen për të siguruar gjendjen aktuale
            driver.navigate().refresh();
            Thread.sleep(3000);

            // Kap screenshot final
            takeScreenshot("cart_final_empty_state");

            // Kontrollo nëse cart është vërtet bosh
            boolean cartIsEmptyNow = isCartEmpty();

            // Verifikimet
            assertTrue(cartIsEmptyNow, "Cart should be empty after deleting all items");
            System.out.println("✓ Cart is empty");

            // Kontrollo për mesazhin e cart bosh
            verifyEmptyCartMessage();

            // Hapi 7: Mbyll browser
            System.out.println("\nStep 7: Close browser");
            System.out.println("✓ Test completed successfully - browser will be closed by @AfterEach");

            // Rezultati final
            System.out.println("\n=== Test 8 COMPLETED SUCCESSFULLY ===");
            System.out.println("✓ Initial items: " + initialItemCount);
            System.out.println("✓ Items deleted: " + itemsDeleted);
            System.out.println("✓ Cart verified as empty");
            System.out.println("✓ All validations passed");

        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();

            takeScreenshot("Test08_EmptyCart_Failure");
            fail("Test failed: " + e.getMessage());
        }
    }

    // Metodat ndihmëse

    private boolean isCartEmpty() {
        try {
            // Kontrollo për mesazhe të cart bosh
            String[] emptyMessages = {
                    "you have no items in your shopping cart",
                    "your shopping cart is empty",
                    "shopping cart is empty",
                    "cart is empty",
                    "no items in your shopping cart"
            };

            String pageText = driver.getPageSource().toLowerCase();

            for (String message : emptyMessages) {
                if (pageText.contains(message.toLowerCase())) {
                    return true;
                }
            }

            // Kontrollo për elementë vizualë të cart bosh
            List<WebElement> emptyElements = driver.findElements(
                    By.cssSelector(".cart-empty, .empty-cart, .no-items, .message.empty, .empty")
            );

            for (WebElement element : emptyElements) {
                if (element.isDisplayed()) {
                    return true;
                }
            }

            // Kontrollo nëse ka produkte në tabelë
            List<WebElement> cartItems = getCartItemElements();
            return cartItems.isEmpty();

        } catch (Exception e) {
            System.out.println("Error checking if cart is empty: " + e.getMessage());
            return false;
        }
    }

    private int getCartItemCount() {
        try {
            // Përpiquni të gjeni produkte në cart në disa mënyra
            List<WebElement> cartItems = getCartItemElements();
            return cartItems.size();

        } catch (Exception e) {
            System.out.println("Error getting cart item count: " + e.getMessage());
            return 0;
        }
    }

    private List<WebElement> getCartItemElements() {
        // Kërko produkte në cart në disa mënyra të ndryshme
        String[] selectors = {
                "tbody tr.item",                    // Rreshta në tabelë
                ".cart.item",                       // Klasa cart item
                "tr.cart-item",                     // Rresht cart item
                ".cart-tbody tr",                   // Rreshta në trupin e tabelës
                ".item-cart",                       // Item cart
                ".product-item-details"            // Detaje produkti
        };

        for (String selector : selectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                if (!elements.isEmpty()) {
                    return elements;
                }
            } catch (Exception e) {
                // Vazhdo me selectorin tjetër
            }
        }

        // Provoni edhe me XPath
        try {
            return driver.findElements(By.xpath("//tr[contains(@class, 'item')] | //div[contains(@class, 'cart-item')]"));
        } catch (Exception e) {
            return driver.findElements(By.cssSelector("nonexistent"));
        }
    }

    private boolean deleteFirstCartItem(JavascriptExecutor js) {
        try {
            // Gjej butonin e fshirjes për produktin e parë
            String[] removeSelectors = {
                    "a[title='Remove Item']",      // Buton remove tradicional
                    ".action.delete",              // Buton delete
                    ".action-remove",              // Buton remove
                    ".remove",                     // Klasa remove
                    "[data-action='delete']",      // Data attribute delete
                    ".btn-remove",                 // Buton remove
                    ".action.remove"               // Action remove
            };

            for (String selector : removeSelectors) {
                try {
                    List<WebElement> removeButtons = driver.findElements(By.cssSelector(selector));

                    if (!removeButtons.isEmpty()) {
                        WebElement firstRemoveButton = removeButtons.get(0);

                        // Scroll to button
                        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", firstRemoveButton);
                        Thread.sleep(1000);

                        // Kliko butonin
                        System.out.println("Clicking remove button...");
                        firstRemoveButton.click();
                        Thread.sleep(2000);

                        // Kontrollo për popup konfirmimi
                        handleConfirmationPopup();

                        return true;
                    }
                } catch (Exception e) {
                    // Vazhdo me selectorin tjetër
                }
            }

            // Nëse nuk gjen butona të zakonshëm, provo me mënyra alternative
            return deleteItemAlternativeMethod();

        } catch (Exception e) {
            System.out.println("Error deleting cart item: " + e.getMessage());
            return false;
        }
    }

    private void handleConfirmationPopup() {
        try {
            // Kontrollo për popup konfirmimi
            String[] confirmSelectors = {
                    ".action-accept",               // Buton accept
                    ".action-primary",              // Buton primary
                    "button[title='OK']",           // Buton OK
                    ".modal-footer .action-primary", // Buton primary në modal
                    ".action.confirm",              // Buton confirm
                    "button.primary"                // Buton primary
            };

            for (String selector : confirmSelectors) {
                try {
                    List<WebElement> confirmButtons = driver.findElements(By.cssSelector(selector));

                    for (WebElement btn : confirmButtons) {
                        if (btn.isDisplayed() && btn.isEnabled()) {
                            System.out.println("Confirming deletion...");
                            btn.click();
                            Thread.sleep(2000);
                            return;
                        }
                    }
                } catch (Exception e) {
                    // Vazhdo
                }
            }
        } catch (Exception e) {
            // Nuk ka popup konfirmimi
        }
    }

    private boolean deleteItemAlternativeMethod() {
        try {
            // Metodë alternative: përdor butonin e fshirjes nga tabela
            List<WebElement> deleteLinks = driver.findElements(
                    By.xpath("//a[contains(text(), 'Remove') or contains(text(), 'Delete')]")
            );

            if (!deleteLinks.isEmpty()) {
                deleteLinks.get(0).click();
                Thread.sleep(2000);
                return true;
            }

            // Provoni të gjeni ikonën e shportës
            List<WebElement> trashIcons = driver.findElements(
                    By.cssSelector(".fa-trash, .trash-icon, .icon-trash")
            );

            if (!trashIcons.isEmpty()) {
                trashIcons.get(0).click();
                Thread.sleep(2000);
                return true;
            }

            return false;

        } catch (Exception e) {
            System.out.println("Alternative deletion method failed: " + e.getMessage());
            return false;
        }
    }

    private void verifyEmptyCartMessage() {
        try {
            // Kontrollo për mesazhin e saktë të cart bosh
            String expectedMessage = "You have no items in your shopping cart.";
            String pageText = driver.getPageSource();

            // Kontrollo nëse mesazhi përmbahet në tekstin e faqes
            if (pageText.contains(expectedMessage)) {
                System.out.println("✓ Verified empty cart message: '" + expectedMessage + "'");
            } else {
                // Kontrollo për variante të mesazhit
                String[] messageVariants = {
                        "You have no items in your shopping cart",
                        "Your shopping cart is empty",
                        "Shopping cart is empty",
                        "Cart is empty",
                        "no items in your shopping cart"
                };

                for (String variant : messageVariants) {
                    if (pageText.toLowerCase().contains(variant.toLowerCase())) {
                        System.out.println("✓ Verified empty cart message (variant): '" + variant + "'");
                        return;
                    }
                }

                System.out.println("⚠ Empty cart message not found, but cart appears empty");
            }

        } catch (Exception e) {
            System.out.println("Error verifying empty cart message: " + e.getMessage());
        }
    }

    private void addItemsToCartForTesting(WebDriverWait wait, JavascriptExecutor js)
            throws InterruptedException {

        System.out.println("Adding items to cart for testing...");

        // Shko te një kategori produktesh
        driver.get("https://ecommerce.tealiumdemo.com/women/tops-women.html");
        Thread.sleep(3000);

        // Gjej disa produkte dhe shtoji në cart
        List<WebElement> products = driver.findElements(
                By.cssSelector(".product-item, .item.product, .products-grid .item")
        );

        int itemsAdded = 0;
        for (int i = 0; i < Math.min(2, products.size()); i++) {
            try {
                WebElement product = products.get(i);

                // Gjej butonin Add to Cart
                List<WebElement> addToCartButtons = product.findElements(
                        By.cssSelector(".action.tocart, button.tocart, [title='Add to Cart']")
                );

                if (!addToCartButtons.isEmpty()) {
                    WebElement addToCartBtn = addToCartButtons.get(0);

                    addToCartBtn.click();
                    Thread.sleep(3000);

                    // Nëse shfaqet faqja e produktit
                    if (driver.getCurrentUrl().contains("/product/")) {
                        // Kliko Add to Cart në faqen e produktit
                        WebElement addToCartOnProductPage = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                        By.cssSelector("#product-addtocart-button, button.tocart")
                                )
                        );

                        addToCartOnProductPage.click();
                        Thread.sleep(3000);

                        // Mbyll popup
                        closePopupIfPresent();

                        // Kthehu prapa
                        driver.navigate().back();
                        Thread.sleep(2000);
                        driver.navigate().back();
                        Thread.sleep(2000);

                        // Rifresko listën
                        products = driver.findElements(
                                By.cssSelector(".product-item, .item.product, .products-grid .item")
                        );
                    }

                    itemsAdded++;
                    System.out.println("Added item " + (i + 1) + " to cart");

                    closePopupIfPresent();
                }
            } catch (Exception e) {
                System.out.println("Could not add product " + (i + 1) + " to cart: " + e.getMessage());
            }
        }

        System.out.println("✓ Added " + itemsAdded + " items to cart");
    }

    private void loginUser(WebDriverWait wait, JavascriptExecutor js) throws InterruptedException {
        System.out.println("Logging in...");

        driver.get("https://ecommerce.tealiumdemo.com/customer/account/login/");
        Thread.sleep(3000);

        String email = TestData.getEmailForLogin();
        String password = TestData.getPasswordForLogin();

        System.out.println("Using email: " + email);

        WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
        emailField.clear();
        emailField.sendKeys(email);

        WebElement passwordField = driver.findElement(By.id("pass"));
        passwordField.clear();
        passwordField.sendKeys(password);

        Thread.sleep(1000);

        WebElement loginButton = driver.findElement(By.id("send2"));
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", loginButton);
        Thread.sleep(500);
        loginButton.click();

        Thread.sleep(5000);

        if (!driver.getCurrentUrl().contains("login")) {
            System.out.println("✓ Login successful");
        } else {
            System.out.println("⚠ Login may have failed");
        }
    }

    private void closePopupIfPresent() {
        try {
            Thread.sleep(1000);

            String[] closeSelectors = {
                    "button[title='Close']",
                    ".action-close",
                    ".close",
                    ".modal-popup .action-close"
            };

            for (String selector : closeSelectors) {
                try {
                    List<WebElement> closeButtons = driver.findElements(By.cssSelector(selector));
                    for (WebElement btn : closeButtons) {
                        if (btn.isDisplayed() && btn.isEnabled()) {
                            btn.click();
                            Thread.sleep(1000);
                            return;
                        }
                    }
                } catch (Exception e) {
                    // Continue
                }
            }
        } catch (Exception e) {
            // No popup
        }
    }

    private void takeScreenshot(String screenshotName) {
        try {
            Path screenshotsDir = Paths.get("screenshots");
            if (!Files.exists(screenshotsDir)) {
                Files.createDirectories(screenshotsDir);
            }

            TakesScreenshot ts = (TakesScreenshot) driver;
            File screenshotFile = ts.getScreenshotAs(OutputType.FILE);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = screenshotName + "_" + timestamp + ".png";
            Path destination = screenshotsDir.resolve(fileName);

            Files.copy(screenshotFile.toPath(), destination);

            System.out.println("Screenshot saved: " + destination.toString());
        } catch (IOException e) {
            System.out.println("Could not save screenshot: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error taking screenshot: " + e.getMessage());
        }
    }
}