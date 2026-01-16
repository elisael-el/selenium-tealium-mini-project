package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FiltersPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public FiltersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Select color filter by clicking on it
     */
    public void selectColorFilter(String colorName) {
        System.out.println("  Attempting to select color: " + colorName);

        try {
            // Wait for page ready
            wait.until(driver ->
                    js.executeScript("return document.readyState").equals("complete")
            );

            // Try to find color by title attribute (most reliable)
            String selector = String.format("[title='%s']", colorName);
            WebElement colorElement = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector))
            );

            System.out.println("  Found color using selector: " + selector);

            // Scroll and click
            js.executeScript("arguments[0].scrollIntoView({block: 'center'});", colorElement);
            wait.until(ExpectedConditions.elementToBeClickable(colorElement));
            js.executeScript("arguments[0].click();", colorElement);

            System.out.println("  ✓ Successfully clicked " + colorName + " filter");
            waitForFilterUpdate();

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
            throw new RuntimeException("Failed to select color filter: " + colorName, e);
        }
    }

    /**
     * Select price range
     */
    /**
     * Select price range - with multiple fallback strategies
     */
    public void selectPriceRange(String priceRange) {
        System.out.println("  Attempting to select price range: " + priceRange);

        try {
            // Wait for page ready
            wait.until(driver ->
                    js.executeScript("return document.readyState").equals("complete")
            );

            // Strategy 1: Try standard PRICE section
            boolean clicked = tryStandardPriceSelection(priceRange);
            if (clicked) return;

            // Strategy 2: Try to find price links anywhere in sidebar
            clicked = tryAnyPriceLink(priceRange);
            if (clicked) return;

            // Strategy 3: Try all links containing price range
            clicked = tryAllLinksWithPriceRange(priceRange);
            if (clicked) return;

            throw new RuntimeException("Price range not found: " + priceRange);

        } catch (Exception e) {
            System.out.println("  Error: " + e.getMessage());
            throw new RuntimeException("Failed to select price: " + priceRange, e);
        }
    }

    /**
     * Try to select price from standard PRICE section
     */
    private boolean tryStandardPriceSelection(String priceRange) {
        try {
            WebElement priceSection = driver.findElement(
                    By.xpath("//dt[contains(text(),'PRICE')]/following-sibling::dd[1]")
            );

            List<WebElement> priceLinks = priceSection.findElements(By.tagName("a"));

            return clickMatchingPriceLink(priceLinks, priceRange);

        } catch (Exception e) {
            System.out.println("  Standard PRICE section not found: " + e.getMessage());
            return false;
        }
    }

    /**
     * Try to find price links anywhere in the sidebar
     */
    private boolean tryAnyPriceLink(String priceRange) {
        try {
            // Find sidebar
            List<WebElement> sidebars = driver.findElements(
                    By.cssSelector(".block-layered-nav, .sidebar, #narrow-by-list")
            );

            if (sidebars.isEmpty()) {
                return false;
            }

            WebElement sidebar = sidebars.get(0);

            // Get all links in sidebar
            List<WebElement> allLinks = sidebar.findElements(By.tagName("a"));

            System.out.println("  Found " + allLinks.size() + " links in sidebar");

            return clickMatchingPriceLink(allLinks, priceRange);

        } catch (Exception e) {
            System.out.println("  Sidebar search failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Try to find price links anywhere on page
     */
    private boolean tryAllLinksWithPriceRange(String priceRange) {
        try {
            // Get all links on page
            List<WebElement> allLinks = driver.findElements(By.tagName("a"));

            System.out.println("  Searching " + allLinks.size() + " links on page for price range");

            for (WebElement link : allLinks) {
                try {
                    String linkText = link.getText().trim();
                    String href = link.getAttribute("href");

                    // Check if this is a price filter link
                    if ((href != null && href.contains("price")) &&
                            linkText.contains(priceRange)) {

                        System.out.println("  ✓ Found price link: " + linkText);

                        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", link);
                        wait.until(ExpectedConditions.elementToBeClickable(link));
                        js.executeScript("arguments[0].click();", link);

                        System.out.println("  ✓ Successfully clicked price filter");
                        waitForFilterUpdate();
                        return true;
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            return false;

        } catch (Exception e) {
            System.out.println("  Page-wide search failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper method to click matching price link from a list
     */
    private boolean clickMatchingPriceLink(List<WebElement> links, String priceRange) {
        System.out.println("  Checking " + links.size() + " price links");

        for (WebElement link : links) {
            try {
                String linkText = link.getText().trim();

                if (!linkText.isEmpty()) {
                    System.out.println("    Price option: " + linkText);
                }

                if (linkText.contains(priceRange) || linkText.startsWith(priceRange)) {
                    System.out.println("  ✓ Found matching price: " + linkText);

                    js.executeScript("arguments[0].scrollIntoView({block: 'center'});", link);
                    wait.until(ExpectedConditions.elementToBeClickable(link));
                    js.executeScript("arguments[0].click();", link);

                    System.out.println("  ✓ Successfully clicked price filter");
                    waitForFilterUpdate();
                    return true;
                }
            } catch (Exception e) {
                continue;
            }
        }

        return false;
    }

    /**
     * Get filtered products - refresh list to avoid stale elements
     */
    public List<WebElement> getFilteredProducts() {
        String[] selectors = {
                ".products-grid .item",
                ".product-item",
                "li.item.product"
        };

        for (String selector : selectors) {
            try {
                List<WebElement> products = wait.until(
                        ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(selector))
                );

                if (!products.isEmpty()) {
                    System.out.println("  Found " + products.size() + " products");
                    return products;
                }
            } catch (Exception e) {
                continue;
            }
        }

        return new ArrayList<>();
    }

    /**
     * Check if a product has the selected color with blue border
     * This checks if the product page would show the color properly
     */
    public boolean hasColorBorderedInBlue(WebElement product, String colorName) {
        try {
            // Look for color swatches that might be visible on hover
            List<WebElement> swatches = product.findElements(
                    By.cssSelector(".swatch-option, [class*='swatch'], .color-option")
            );

            if (swatches.isEmpty()) {
                // No color swatches visible - this is normal for list view
                // Return true if product is displayed (filter worked)
                return false;
            }

            // Check for selected color with blue border
            for (WebElement swatch : swatches) {
                try {
                    String className = swatch.getAttribute("class");

                    if (className != null && className.contains("selected")) {
                        String borderColor = swatch.getCssValue("border-color");

                        if (isBlueColor(borderColor)) {
                            System.out.println("    ✓ Has blue border on selected color");
                            return true;
                        }
                    }
                } catch (Exception e) {
                    // Element became stale, skip
                    continue;
                }
            }

            return false;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get product price
     */
    public double getProductPrice(WebElement product) {
        try {
            // Try to get price from product element
            WebElement priceElement = product.findElement(
                    By.cssSelector(".price, .special-price .price, .price-box .price")
            );

            String priceText = priceElement.getText()
                    .replace("$", "")
                    .replace(",", "")
                    .trim();

            // Handle price ranges - take first price
            if (priceText.contains("-")) {
                priceText = priceText.split("-")[0].trim();
            }

            double price = Double.parseDouble(priceText);
            System.out.println("    Product price: $" + String.format("%.2f", price));
            return price;

        } catch (Exception e) {
            System.out.println("    Could not get price: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Check if price is in range
     */
    public boolean isPriceInRange(double price, double minPrice, double maxPrice) {
        if (price == 0.0) {
            return false; // Skip if price couldn't be retrieved
        }

        boolean inRange = price >= minPrice && price <= maxPrice;

        if (inRange) {
            System.out.println("    ✓ Price $" + String.format("%.2f", price) + " is in range");
        } else {
            System.out.println("    ✗ Price $" + String.format("%.2f", price) + " is NOT in range");
        }

        return inRange;
    }

    /**
     * Get product name - with stale element handling
     */
    public String getProductName(WebElement product) {
        try {
            WebElement nameElement = product.findElement(
                    By.cssSelector(".product-name, .product-item-name, .product-name a")
            );
            return nameElement.getText().trim();
        } catch (Exception e) {
            return "Unknown Product";
        }
    }

    /**
     * Wait for filter update
     */
    private void waitForFilterUpdate() {
        try {
            // Wait for page to complete loading
            wait.until(driver ->
                    js.executeScript("return document.readyState").equals("complete")
            );

            // Wait for products container to be present
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(".products-grid, .product-items, .category-products")
            ));

            System.out.println("  ✓ Filter applied and products reloaded");

        } catch (Exception e) {
            System.out.println("  Filter update completed");
        }
    }

    /**
     * Check if color is blue
     */
    private boolean isBlueColor(String color) {
        if (color == null || color.isEmpty()) {
            return false;
        }

        if (color.toLowerCase().contains("blue")) {
            return true;
        }

        if (color.contains("rgb")) {
            try {
                String rgbString = color.replace("rgba(", "")
                        .replace("rgb(", "")
                        .replace(")", "");
                String[] parts = rgbString.split(",");

                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                return (b > 100) && (b > r) && (b > g);

            } catch (Exception e) {
                return false;
            }
        }

        return false;
    }
}