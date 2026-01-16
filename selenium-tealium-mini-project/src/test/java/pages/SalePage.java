package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object for Sale Products Page
 * Contains methods to interact with and verify sale product styling
 */
public class SalePage {

    private WebDriver driver;
    private WebDriverWait wait;

    // Locators - try multiple selectors for robustness
    private String[] productSelectors = {
            ".products-grid .item",
            ".product-item",
            ".products-list .item",
            "li.item.product"
    };

    public SalePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    /**
     * Get all sale products on the page
     * Tries multiple selectors to find products
     * @return List of sale product WebElements
     */
    public List<WebElement> getSaleProducts() {
        List<WebElement> products = new ArrayList<>();

        // Try each selector until we find products
        for (String selector : productSelectors) {
            try {
                By locator = By.cssSelector(selector);
                wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
                products = driver.findElements(locator);

                if (!products.isEmpty()) {
                    System.out.println("  Found " + products.size() + " products using selector: " + selector);
                    return products;
                }
            } catch (Exception e) {
                // Try next selector
                continue;
            }
        }

        System.out.println("  Warning: No products found with any selector");
        return products;
    }

    /**
     * Check if a specific product has both old and special prices
     * @param product The product WebElement to check
     * @return true if product has both prices, false otherwise
     */
    public boolean hasMultiplePrices(WebElement product) {
        try {
            // Look for old price within this product
            List<WebElement> oldPrices = product.findElements(
                    By.cssSelector(".old-price .price, .regular-price .price, .price.old-price")
            );

            // Look for special price within this product
            List<WebElement> specialPrices = product.findElements(
                    By.cssSelector(".special-price .price, .sale-price .price, .price.special-price")
            );

            boolean hasMultiple = !oldPrices.isEmpty() && !specialPrices.isEmpty();

            if (hasMultiple) {
                System.out.println("    ✓ Product has both old price and special price");
            } else {
                System.out.println("    ✗ Product missing prices - Old: " +
                        oldPrices.size() + ", Special: " + specialPrices.size());
            }

            return hasMultiple;

        } catch (Exception e) {
            System.out.println("    Error checking prices: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get the old price element from a product
     * @param product The product WebElement
     * @return Old price WebElement or null
     */
    public WebElement getOldPrice(WebElement product) {
        try {
            List<WebElement> oldPrices = product.findElements(
                    By.cssSelector(".old-price .price, .regular-price .price, .price.old-price")
            );

            if (!oldPrices.isEmpty()) {
                return oldPrices.get(0);
            }
        } catch (Exception e) {
            System.out.println("    Error getting old price: " + e.getMessage());
        }
        return null;
    }

    /**
     * Get the special price element from a product
     * @param product The product WebElement
     * @return Special price WebElement or null
     */
    public WebElement getSpecialPrice(WebElement product) {
        try {
            List<WebElement> specialPrices = product.findElements(
                    By.cssSelector(".special-price .price, .sale-price .price, .price.special-price")
            );

            if (!specialPrices.isEmpty()) {
                return specialPrices.get(0);
            }
        } catch (Exception e) {
            System.out.println("    Error getting special price: " + e.getMessage());
        }
        return null;
    }

    /**
     * Check if old price has strikethrough text decoration
     * @param oldPrice The old price WebElement
     * @return true if strikethrough, false otherwise
     */
    public boolean isOldPriceStrikethrough(WebElement oldPrice) {
        if (oldPrice == null) {
            return false;
        }

        String textDecoration = oldPrice.getCssValue("text-decoration");
        String textDecorationLine = oldPrice.getCssValue("text-decoration-line");

        System.out.println("      Old price text-decoration: " + textDecoration);
        System.out.println("      Old price text-decoration-line: " + textDecorationLine);

        // Check both properties as different browsers may use different ones
        boolean hasStrikethrough = textDecoration.contains("line-through") ||
                textDecorationLine.contains("line-through");

        System.out.println("      Old price has strikethrough: " + hasStrikethrough);
        return hasStrikethrough;
    }

    /**
     * Check if old price has grey color
     * @param oldPrice The old price WebElement
     * @return true if grey, false otherwise
     */
    public boolean isOldPriceGrey(WebElement oldPrice) {
        if (oldPrice == null) {
            return false;
        }

        String color = oldPrice.getCssValue("color");
        System.out.println("      Old price color: " + color);

        // Check for common grey color keywords
        if (color.contains("gray") || color.contains("grey")) {
            System.out.println("      Old price is grey (keyword)");
            return true;
        }

        // Parse RGB values
        if (color.contains("rgba") || color.contains("rgb")) {
            try {
                // Extract RGB values
                String rgbString = color.replace("rgba(", "").replace("rgb(", "").replace(")", "");
                String[] parts = rgbString.split(",");

                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                System.out.println("      RGB values: R=" + r + ", G=" + g + ", B=" + b);

                // Grey colors have similar RGB values
                // All values should be within 50 of each other
                int maxDiff = Math.max(Math.abs(r - g), Math.max(Math.abs(r - b), Math.abs(g - b)));

                // Grey colors are typically in range 80-180 (not pure black or white)
                boolean isSimilar = maxDiff <= 50;
                boolean isGreyRange = (r >= 80 && r <= 200) &&
                        (g >= 80 && g <= 200) &&
                        (b >= 80 && b <= 200);

                boolean isGrey = isSimilar && isGreyRange;
                System.out.println("      Is grey by RGB: " + isGrey +
                        " (similar: " + isSimilar + ", range: " + isGreyRange + ")");

                return isGrey;

            } catch (Exception e) {
                System.out.println("      Error parsing color: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Check if special price does NOT have strikethrough
     * @param specialPrice The special price WebElement
     * @return true if NOT strikethrough, false otherwise
     */
    public boolean isSpecialPriceNotStrikethrough(WebElement specialPrice) {
        if (specialPrice == null) {
            return false;
        }

        String textDecoration = specialPrice.getCssValue("text-decoration");
        String textDecorationLine = specialPrice.getCssValue("text-decoration-line");

        System.out.println("      Special price text-decoration: " + textDecoration);
        System.out.println("      Special price text-decoration-line: " + textDecorationLine);

        // Should NOT contain line-through
        boolean noStrikethrough = !textDecoration.contains("line-through") &&
                !textDecorationLine.contains("line-through");

        System.out.println("      Special price has NO strikethrough: " + noStrikethrough);
        return noStrikethrough;
    }

    /**
     * Check if special price has blue color
     * @param specialPrice The special price WebElement
     * @return true if blue, false otherwise
     */
    public boolean isSpecialPriceBlue(WebElement specialPrice) {
        if (specialPrice == null) {
            return false;
        }

        String color = specialPrice.getCssValue("color");
        System.out.println("      Special price color: " + color);

        // Check for common blue color keywords
        if (color.contains("blue")) {
            System.out.println("      Special price is blue (keyword)");
            return true;
        }

        // Parse RGB values
        if (color.contains("rgba") || color.contains("rgb")) {
            try {
                // Extract RGB values
                String rgbString = color.replace("rgba(", "").replace("rgb(", "").replace(")", "");
                String[] parts = rgbString.split(",");

                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                System.out.println("      RGB values: R=" + r + ", G=" + g + ", B=" + b);

                // Blue color: B value should be significantly higher than R and G
                // Typical blue: B > 100, and B > R and B > G
                boolean isBlue = (b > 100) && (b > r) && (b > g);

                System.out.println("      Is blue by RGB: " + isBlue +
                        " (B=" + b + " > R=" + r + " and B > G=" + g + ")");

                return isBlue;

            } catch (Exception e) {
                System.out.println("      Error parsing color: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Get product name for logging purposes
     * @param product The product WebElement
     * @return Product name or "Unknown"
     */
    public String getProductName(WebElement product) {
        try {
            WebElement nameElement = product.findElement(
                    By.cssSelector(".product-name, h2.product-name, .product-item-name")
            );
            return nameElement.getText().trim();
        } catch (Exception e) {
            return "Unknown Product";
        }
    }
}