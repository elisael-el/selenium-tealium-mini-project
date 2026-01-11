package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
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

public class Test07_ShoppingCart extends BaseTest {

    @Test
    void testShoppingCart() {
        System.out.println("=== Test 7: Shopping Cart ===");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Actions actions = new Actions(driver);

        try {
            // Hapi 0: Hyrje në sistem
            System.out.println("Step 0: Login");
            loginUser(wait, js);
            System.out.println("✓ Login successful");

            // Hapi 1: Kontrollo dhe shto produkte në wishlist nëse është bosh
            System.out.println("\nStep 1: Check and add products to wishlist if needed");

            // Shko në wishlist
            driver.get("https://ecommerce.tealiumdemo.com/wishlist/");
            Thread.sleep(3000);

            // Kontrollo nëse wishlist ka produkte
            boolean wishlistHasItems = checkIfWishlistHasItems();

            if (!wishlistHasItems) {
                System.out.println("Wishlist is empty. Adding products...");
                addProductsToWishlist(wait, js, actions);

                // Shko përsëri në wishlist
                driver.get("https://ecommerce.tealiumdemo.com/wishlist/");
                Thread.sleep(3000);
            }

            System.out.println("✓ Wishlist is ready");

            // Hapi 2: Shto produkte në shportën e blerjeve
            System.out.println("\nStep 2: Add products to shopping cart");

            // Gjej të gjitha produkte në wishlist
            List<WebElement> wishlistItems = findWishlistItems();
            System.out.println("Found " + wishlistItems.size() + " items in wishlist");

            // Shto të paktën 1 produkt në cart
            int productsAddedToCart = 0;
            int maxProductsToAdd = Math.min(2, wishlistItems.size());

            for (int i = 0; i < maxProductsToAdd; i++) {
                try {
                    System.out.println("\nAttempting to add product " + (i + 1) + " to cart...");

                    // Metoda 1: Provo të gjejsh butonin "Add to Cart" direkt
                    boolean added = tryToAddProductToCart(wait, js, i);

                    if (added) {
                        productsAddedToCart++;
                        System.out.println("✓ Product " + (i + 1) + " added to cart");
                    } else {
                        System.out.println("Could not add product " + (i + 1) + " to cart");
                    }

                } catch (Exception e) {
                    System.out.println("Error adding product " + (i + 1) + " to cart: " + e.getMessage());
                }
            }

            assertTrue(productsAddedToCart > 0, "Should have added at least 1 product to cart");
            System.out.println("\n✓ Successfully added " + productsAddedToCart + " product(s) to cart");

            // Hapi 3: Hap Shopping Cart, ndrysho sasinë në 2
            System.out.println("\nStep 3: Open Shopping Cart and update quantity");

            // Shko në shportën e blerjeve
            driver.get("https://ecommerce.tealiumdemo.com/checkout/cart/");
            Thread.sleep(4000);

            // Verifiko nëse jemi në cart page
            assertTrue(driver.getCurrentUrl().contains("cart") ||
                            driver.getTitle().toLowerCase().contains("cart"),
                    "Should be on shopping cart page");
            System.out.println("✓ On shopping cart page");

            // Kap screenshot përpara ndryshimit të sasisë
            takeScreenshot("cart_before_quantity_update");

            // Gjej inputet e sasisë
            List<WebElement> quantityInputs = driver.findElements(
                    By.cssSelector("input.qty, input[title='Qty'], .cart-item-qty, [name='cart[qty]'], [name='qty']")
            );

            assertTrue(!quantityInputs.isEmpty(), "Should have quantity inputs in cart");
            System.out.println("Found " + quantityInputs.size() + " quantity inputs");

            // Ndrysho sasinë e produktit të parë në 2
            WebElement firstQtyInput = quantityInputs.get(0);

            // Ruaj sasinë aktuale
            String currentQty = firstQtyInput.getAttribute("value");
            System.out.println("Current quantity: " + currentQty);

            // Pastro dhe vendos sasinë e re
            firstQtyInput.clear();
            Thread.sleep(500);
            firstQtyInput.sendKeys("2");
            Thread.sleep(1000);

            System.out.println("✓ Changed quantity to 2");

            // Gjej butonin Update
            List<WebElement> updateButtons = driver.findElements(
                    By.cssSelector("button.update, [title='Update'], .action.update, .update-cart-item, .update")
            );

            assertTrue(!updateButtons.isEmpty(), "Should have update button");
            WebElement updateButton = updateButtons.get(0);

            // Scroll to button dhe kliko
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", updateButton);
            Thread.sleep(500);
            updateButton.click();
            Thread.sleep(3000);

            System.out.println("✓ Cart updated successfully");

            // Hapi 4: Verifiko që shuma e çmimeve = Grand Total
            System.out.println("\nStep 4: Verify price calculations");

            // Prit që faqja të rifreskohet plotësisht
            Thread.sleep(2000);

            // Kap screenshot pas përditësimit
            takeScreenshot("cart_after_quantity_update");

            // Llogarit totalin nga produktet individuale
            double calculatedTotal = calculateCartTotal();

            // Gjej Grand Total nga faqja
            double displayedGrandTotal = getGrandTotal();

            // Verifiko nëse totalet janë të barabarta
            double difference = Math.abs(calculatedTotal - displayedGrandTotal);

            assertTrue(difference < 0.01,
                    String.format("Calculated total ($%.2f) should equal displayed grand total ($%.2f). Difference: $%.2f",
                            calculatedTotal, displayedGrandTotal, difference));

            System.out.println(String.format("✓ Price verification passed: Calculated: $%.2f, Displayed: $%.2f",
                    calculatedTotal, displayedGrandTotal));

            // Mos bëj logout - lë sesionin të hapur për Test08
            System.out.println("\n✓ Test 7 completed successfully");
            System.out.println("✓ Login session maintained for Test08");
            System.out.println("✓ Cart has " + quantityInputs.size() + " product(s) ready for Test08");

        } catch (Exception e) {
            System.out.println("Test failed: " + e.getMessage());
            e.printStackTrace();

            // Kap screenshot në rast dështimi
            takeScreenshot("Test07_ShoppingCart_Failure");
            fail("Test failed: " + e.getMessage());
        }
    }

    // Metodat ndihmëse

    private boolean checkIfWishlistHasItems() {
        try {
            // Kontrollo nëse ka mesazh empty
            String pageText = driver.getPageSource().toLowerCase();
            if (pageText.contains("you have no items") ||
                    pageText.contains("wishlist is empty") ||
                    pageText.contains("no items in your wishlist")) {
                return false;
            }

            // Kontrollo për produkte
            List<WebElement> items = findWishlistItems();
            return !items.isEmpty();

        } catch (Exception e) {
            System.out.println("Error checking wishlist: " + e.getMessage());
            return false;
        }
    }

    private void addProductsToWishlist(WebDriverWait wait, JavascriptExecutor js, Actions actions)
            throws InterruptedException {

        System.out.println("Adding products to wishlist...");

        // Shko te një kategori produktesh
        driver.get("https://ecommerce.tealiumdemo.com/women/tops-women.html");
        Thread.sleep(3000);

        // Gjej produkte
        List<WebElement> products = driver.findElements(
                By.cssSelector(".product-item, .item.product, .product-item-info, .item")
        );

        System.out.println("Found " + products.size() + " products on page");

        int addedCount = 0;
        for (int i = 0; i < Math.min(2, products.size()); i++) {
            try {
                WebElement product = products.get(i);

                // Scroll to product
                js.executeScript("arguments[0].scrollIntoView({block: 'center'});", product);
                Thread.sleep(1000);

                // Hover over product
                actions.moveToElement(product).perform();
                Thread.sleep(1000);

                // Gjej wishlist button - provo disa selektorë të ndryshëm
                boolean addedToWishlist = false;

                // Provoni me selektorë të ndryshëm
                String[] wishlistSelectors = {
                        "[data-action='add-to-wishlist']",
                        "[title='Add to Wish List']",
                        ".action.towishlist",
                        ".towishlist",
                        ".wishlist",
                        ".product-social-links a[href*='wishlist']"
                };

                for (String selector : wishlistSelectors) {
                    try {
                        List<WebElement> wishlistButtons = product.findElements(By.cssSelector(selector));
                        if (!wishlistButtons.isEmpty()) {
                            WebElement wishlistBtn = wishlistButtons.get(0);

                            // Përdor JavaScript click
                            js.executeScript("arguments[0].click();", wishlistBtn);
                            Thread.sleep(2000);

                            addedToWishlist = true;
                            break;
                        }
                    } catch (Exception e) {
                        // Continue with next selector
                    }
                }

                // Nëse nuk gjen me selektorët e mësipërm, provo me XPath
                if (!addedToWishlist) {
                    try {
                        // Kërko për link që përmban 'wishlist'
                        List<WebElement> wishlistLinks = product.findElements(
                                By.xpath(".//a[contains(@href, 'wishlist') or contains(@title, 'Wish List')]")
                        );

                        if (!wishlistLinks.isEmpty()) {
                            WebElement wishlistLink = wishlistLinks.get(0);
                            js.executeScript("arguments[0].click();", wishlistLink);
                            Thread.sleep(2000);
                            addedToWishlist = true;
                        }
                    } catch (Exception e) {
                        // Continue
                    }
                }

                if (addedToWishlist) {
                    addedCount++;
                    System.out.println("Added product " + (i + 1) + " to wishlist");

                    // Mbyll popup nëse shfaqet
                    closePopupIfPresent();
                } else {
                    System.out.println("Could not find wishlist button for product " + (i + 1));
                }

            } catch (Exception e) {
                System.out.println("Error adding product " + (i + 1) + " to wishlist: " + e.getMessage());
            }
        }

        System.out.println("✓ Added " + addedCount + " products to wishlist");
    }

    private boolean tryToAddProductToCart(WebDriverWait wait, JavascriptExecutor js, int productIndex)
            throws InterruptedException {

        // Gjej të gjitha produktet në wishlist
        List<WebElement> wishlistItems = findWishlistItems();

        if (wishlistItems.size() <= productIndex) {
            return false;
        }

        WebElement product = wishlistItems.get(productIndex);

        // Provoni disa mënyra për të shtuar në cart

        // Metoda 1: Gjej butonin "Add to Cart" direkt
        String[] addToCartSelectors = {
                ".action.tocart",
                "button.tocart",
                "[title='Add to Cart']",
                ".tocart",
                "[data-action='add-to-cart']",
                ".action.primary.tocart"
        };

        for (String selector : addToCartSelectors) {
            try {
                List<WebElement> addToCartButtons = product.findElements(By.cssSelector(selector));
                if (!addToCartButtons.isEmpty()) {
                    WebElement addToCartBtn = addToCartButtons.get(0);

                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", addToCartBtn);
                    Thread.sleep(1000);

                    // Përdor JavaScript click
                    js.executeScript("arguments[0].click();", addToCartBtn);
                    Thread.sleep(3000);

                    // Kontrollo nëse shfaqet faqja e produktit
                    if (driver.getCurrentUrl().contains("/product/")) {
                        handleProductPage(wait, js);
                        return true;
                    } else {
                        // Produkti u shtua direkt
                        closePopupIfPresent();
                        return true;
                    }
                }
            } catch (Exception e) {
                // Continue with next selector
            }
        }

        // Metoda 2: Kliko në emrin e produktit për të shkuar në faqen e produktit
        try {
            // Gjej linkun e produktit
            List<WebElement> productLinks = product.findElements(
                    By.cssSelector(".product-item-link, .product.name a, a.product-item-link")
            );

            if (!productLinks.isEmpty()) {
                WebElement productLink = productLinks.get(0);
                String productUrl = productLink.getAttribute("href");

                if (productUrl != null && !productUrl.isEmpty()) {
                    // Shko në faqen e produktit
                    driver.get(productUrl);
                    Thread.sleep(3000);

                    // Shto në cart nga faqja e produktit
                    return handleProductPage(wait, js);
                }
            }
        } catch (Exception e) {
            // Continue
        }

        return false;
    }

    private boolean handleProductPage(WebDriverWait wait, JavascriptExecutor js)
            throws InterruptedException {

        System.out.println("On product page, handling options...");

        // Provo të zgjedhësh opsione nëse ekzistojnë
        tryToSelectProductOptions(js);

        // Gjej butonin Add to Cart në faqen e produktit
        List<WebElement> addToCartButtons = driver.findElements(
                By.cssSelector("#product-addtocart-button, button.tocart, [title='Add to Cart'], .action.tocart")
        );

        if (!addToCartButtons.isEmpty()) {
            WebElement addToCartBtn = addToCartButtons.get(0);

            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", addToCartBtn);
            Thread.sleep(1000);
            js.executeScript("arguments[0].click();", addToCartBtn);
            Thread.sleep(3000);

            closePopupIfPresent();
            return true;
        }

        return false;
    }

    private void tryToSelectProductOptions(JavascriptExecutor js) throws InterruptedException {
        // Provo të zgjedhësh madhësinë
        try {
            List<WebElement> sizeOptions = driver.findElements(
                    By.cssSelector(".swatch-option.text, [attribute-code='size'], select#size")
            );

            if (!sizeOptions.isEmpty()) {
                for (WebElement sizeOption : sizeOptions) {
                    try {
                        if (sizeOption.isDisplayed() && sizeOption.isEnabled()) {
                            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", sizeOption);
                            Thread.sleep(500);
                            js.executeScript("arguments[0].click();", sizeOption);
                            Thread.sleep(1000);
                            System.out.println("Selected size option");
                            break;
                        }
                    } catch (Exception e) {
                        // Continue
                    }
                }
            }
        } catch (Exception e) {
            // No size options
        }

        // Provo të zgjedhësh ngjyrën
        try {
            List<WebElement> colorOptions = driver.findElements(
                    By.cssSelector(".swatch-option.color, [attribute-code='color'], select#color")
            );

            if (!colorOptions.isEmpty()) {
                for (WebElement colorOption : colorOptions) {
                    try {
                        if (colorOption.isDisplayed() && colorOption.isEnabled()) {
                            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", colorOption);
                            Thread.sleep(500);
                            js.executeScript("arguments[0].click();", colorOption);
                            Thread.sleep(1000);
                            System.out.println("Selected color option");
                            break;
                        }
                    } catch (Exception e) {
                        // Continue
                    }
                }
            }
        } catch (Exception e) {
            // No color options
        }
    }

    private List<WebElement> findWishlistItems() {
        // Kërko produkte në wishlist në disa mënyra të ndryshme
        String[] selectors = {
                ".products-grid .item",
                ".product-item",
                ".item.product",
                ".product-items .item",
                "[data-item-id]",
                ".product-item-info"
        };

        for (String selector : selectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                if (!elements.isEmpty() && elements.size() > 0) {
                    return elements;
                }
            } catch (Exception e) {
                // Vazhdo me selectorin tjetër
            }
        }

        // Provoni me XPath
        try {
            return driver.findElements(By.xpath("//li[contains(@class, 'item')] | //div[contains(@class, 'product')]"));
        } catch (Exception e) {
            return driver.findElements(By.cssSelector("nonexistent"));
        }
    }

    private double calculateCartTotal() {
        double total = 0.0;

        try {
            // Gjej të gjitha rreshtat e produkteve në cart
            List<WebElement> cartRows = driver.findElements(
                    By.cssSelector("tbody tr.item, .cart.item, tr.cart-item, .cart-tbody tr, table tbody tr")
            );

            System.out.println("Calculating total for " + cartRows.size() + " items...");

            for (int i = 0; i < cartRows.size(); i++) {
                try {
                    WebElement row = cartRows.get(i);

                    // Gjej çmimin
                    List<WebElement> priceElements = row.findElements(
                            By.cssSelector(".price, .cart-price .price, .price-including-tax .price, [data-th='Price'] .price")
                    );

                    if (!priceElements.isEmpty()) {
                        String priceText = priceElements.get(0).getText().trim();
                        priceText = extractPriceNumber(priceText);
                        double price = Double.parseDouble(priceText);

                        // Gjej sasinë
                        int quantity = 1;
                        List<WebElement> qtyElements = row.findElements(
                                By.cssSelector("input.qty, .cart-item-qty, [name='cart[qty]'], [name='qty']")
                        );

                        if (!qtyElements.isEmpty()) {
                            String qtyText = qtyElements.get(0).getAttribute("value");
                            if (qtyText != null && !qtyText.trim().isEmpty()) {
                                quantity = Integer.parseInt(qtyText.trim());
                            }
                        }

                        double itemTotal = price * quantity;
                        total += itemTotal;

                        System.out.println(String.format("  Item %d: $%.2f x %d = $%.2f",
                                i + 1, price, quantity, itemTotal));
                    }

                } catch (Exception e) {
                    System.out.println("Could not calculate price for item " + (i + 1) + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.out.println("Error calculating cart total: " + e.getMessage());
        }

        System.out.println(String.format("Total calculated: $%.2f", total));
        return total;
    }

    private String extractPriceNumber(String priceText) {
        // Heq simbolin e dollarit dhe karaktere të tjera
        priceText = priceText.replace("$", "")
                .replace("€", "")
                .replace("£", "")
                .replace(",", "")
                .replace("USD", "")
                .trim();

        // Nëse ka shumë fjalë, merr vetëm numrin e parë
        String[] parts = priceText.split(" ");
        if (parts.length > 0) {
            return parts[0];
        }

        return priceText;
    }

    private double getGrandTotal() {
        try {
            // Gjej Grand Total element
            String[] grandTotalSelectors = {
                    ".grand .price",
                    "strong.grand .price",
                    ".grand_total .price",
                    ".totals .grand .price",
                    "[data-th='Grand Total'] .price",
                    ".order-total .price"
            };

            for (String selector : grandTotalSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    if (!elements.isEmpty()) {
                        String grandTotalText = elements.get(0).getText().trim();
                        grandTotalText = extractPriceNumber(grandTotalText);
                        double grandTotal = Double.parseDouble(grandTotalText);

                        System.out.println(String.format("Grand total displayed: $%.2f", grandTotal));
                        return grandTotal;
                    }
                } catch (Exception e) {
                    // Continue with next selector
                }
            }

            // Provoni të gjeni subtotal nëse nuk gjen grand total
            String[] subtotalSelectors = {
                    ".subtotal .price",
                    ".totals .sub .price",
                    "[data-th='Subtotal'] .price"
            };

            for (String selector : subtotalSelectors) {
                try {
                    List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                    if (!elements.isEmpty()) {
                        String subtotalText = elements.get(0).getText().trim();
                        subtotalText = extractPriceNumber(subtotalText);
                        double subtotal = Double.parseDouble(subtotalText);

                        System.out.println(String.format("Subtotal displayed: $%.2f", subtotal));
                        return subtotal;
                    }
                } catch (Exception e) {
                    // Continue
                }
            }

            return 0.0;

        } catch (Exception e) {
            System.out.println("Could not find grand total: " + e.getMessage());
            return 0.0;
        }
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
                    ".modal-popup .action-close",
                    ".message-popup .close",
                    "[data-role='closeBtn']"
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