package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SortingPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By sortDropdown = By.cssSelector(".sorter select, select.sorter");
    private By products = By.cssSelector(".products-grid .item");
    private By productPrices = By.cssSelector(".products-grid .item .price");
    private By addToWishlistButtons = By.cssSelector(".link-wishlist");
    private By accountMenu = By.xpath("//span[text()='Account']");
    private By wishlistLink = By.xpath("//a[contains(text(),'My Wish List')]");

    public SortingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void sortByPrice() {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(sortDropdown));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", dropdown);

            Thread.sleep(500);

            Select select = new Select(dropdown);
            select.selectByVisibleText("Price");
            System.out.println("Selected sort by Price");

            Thread.sleep(3000);

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
            System.out.println("Products reloaded after sorting");

        } catch (Exception e) {
            System.err.println("Error sorting by price: " + e.getMessage());
        }
    }

    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();

        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
            List<WebElement> productElements = driver.findElements(products);

            for (WebElement product : productElements) {
                try {
                    double price = getProductPrice(product);
                    if (price > 0) {
                        prices.add(price);
                    }
                } catch (Exception e) {
                    System.err.println("Could not get price for product: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting product prices: " + e.getMessage());
        }

        return prices;
    }

    private double getProductPrice(WebElement product) {
        try {
            // Provo të gjesh special price (discounted) së pari
            List<WebElement> specialPrices = product.findElements(By.cssSelector(".special-price .price"));
            if (!specialPrices.isEmpty()) {
                String priceText = specialPrices.get(0).getText().replace("$", "").replace(",", "").trim();
                if (!priceText.isEmpty()) {
                    return Double.parseDouble(priceText);
                }
            }

            // Nëse nuk ka special price, merr regular price
            List<WebElement> regularPrices = product.findElements(By.cssSelector(".regular-price .price, .price-box .price"));
            if (!regularPrices.isEmpty()) {
                String priceText = regularPrices.get(0).getText().replace("$", "").replace(",", "").trim();
                if (!priceText.isEmpty()) {
                    return Double.parseDouble(priceText);
                }
            }

            // Fallback: merr çdo .price që gjen
            List<WebElement> anyPrices = product.findElements(By.cssSelector(".price"));
            if (!anyPrices.isEmpty()) {
                String priceText = anyPrices.get(0).getText().replace("$", "").replace(",", "").trim();
                if (!priceText.isEmpty()) {
                    return Double.parseDouble(priceText);
                }
            }

            return 0.0;
        } catch (Exception e) {
            return 0.0;
        }
    }

    public int addFirstTwoToWishlist() {
        int successfullyAdded = 0;

        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(addToWishlistButtons));

            int itemsToAdd = 2;

            for (int i = 0; i < itemsToAdd; i++) {
                try {
                    // Re-navigate to ensure we're on the right page
                    if (i > 0) {
                        driver.navigate().back();
                        Thread.sleep(2000);
                        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(addToWishlistButtons));
                    }

                    // Re-find elements
                    List<WebElement> currentButtons = driver.findElements(addToWishlistButtons);

                    if (currentButtons.size() <= i) {
                        System.out.println("No more wishlist buttons available");
                        break;
                    }

                    WebElement button = currentButtons.get(i);

                    // Scroll to button
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].scrollIntoView({block: 'center'});", button
                    );

                    Thread.sleep(500);

                    // Get product name for logging
                    try {
                        WebElement product = button.findElement(By.xpath("./ancestor::li[contains(@class, 'item')]"));
                        String productName = product.findElement(By.cssSelector("h2.product-name, .product-name")).getText();
                        System.out.println("Adding product to wishlist: " + productName);
                    } catch (Exception e) {
                        System.out.println("Adding product " + (i + 1) + " to wishlist");
                    }

                    // Click with JavaScript
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);

                    // Wait for page load or success message
                    Thread.sleep(3000);

                    // Check if we're redirected to wishlist or login
                    String currentUrl = driver.getCurrentUrl();
                    System.out.println("Current URL after click: " + currentUrl);

                    if (currentUrl.contains("wishlist")) {
                        System.out.println("Successfully added product " + (i + 1) + " to wishlist");
                        successfullyAdded++;
                    } else if (currentUrl.contains("login")) {
                        System.out.println("Redirected to login - session may have expired");
                        break;
                    } else {
                        // Check for success message
                        try {
                            WebElement successMsg = driver.findElement(By.cssSelector("li.success-msg, .success-msg"));
                            if (successMsg.isDisplayed()) {
                                System.out.println("Successfully added product " + (i + 1) + " to wishlist");
                                successfullyAdded++;
                            }
                        } catch (Exception e) {
                            System.out.println("Could not verify success for product " + (i + 1));
                        }
                    }

                } catch (Exception e) {
                    System.err.println("Error adding item " + (i + 1) + " to wishlist: " + e.getMessage());

                    // Check if browser is still alive
                    try {
                        driver.getCurrentUrl();
                    } catch (Exception browserError) {
                        System.err.println("Browser connection lost");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in addFirstTwoToWishlist: " + e.getMessage());
        }

        return successfullyAdded;
    }

    public String getWishlistCount() {
        try {
            // Verify browser is still alive
            driver.getCurrentUrl();

            WebElement accountBtn = wait.until(
                    ExpectedConditions.elementToBeClickable(accountMenu)
            );

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", accountBtn);

            Thread.sleep(1000);

            wait.until(ExpectedConditions.visibilityOfElementLocated(wishlistLink));
            WebElement wishlistElement = driver.findElement(wishlistLink);
            String text = wishlistElement.getText();

            System.out.println("Wishlist text found: " + text);

            // Close menu
            ((JavascriptExecutor) driver).executeScript("document.body.click();");

            return text;
        } catch (Exception e) {
            System.err.println("Error getting wishlist count: " + e.getMessage());

            // Try alternative method - go directly to wishlist
            try {
                driver.get("https://ecommerce.tealiumdemo.com/wishlist/");
                Thread.sleep(2000);

                // Count items on wishlist page
                List<WebElement> wishlistItems = driver.findElements(By.cssSelector(".products-grid .item, tbody tr"));
                if (wishlistItems.size() > 0) {
                    return "My Wish List (" + wishlistItems.size() + " items)";
                }
            } catch (Exception e2) {
                System.err.println("Alternative method also failed: " + e2.getMessage());
            }

            return "";
        }
    }
}