package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class ProductsPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    // Locators - vendos në nivel instance, jo static
    private By products = By.cssSelector(".products-grid li.item");
    private By productImages = By.cssSelector(".product-image");
    private By productNames = By.cssSelector(".product-name");
    private By productPrices = By.cssSelector(".price");
    private By oldPrices = By.cssSelector(".old-price .price");
    private By specialPrices = By.cssSelector(".special-price .price");
    private By addToWishlistButtons = By.cssSelector(".link-wishlist");
    private By addToCartButtons = By.cssSelector("button.btn-cart");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.actions = new Actions(driver);
    }

    public List<WebElement> getAllProducts() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
    }

    public void hoverOverProduct(WebElement product) {
        actions.moveToElement(product).perform();
    }

    public String getProductStyleBeforeHover(WebElement product) {
        return product.getCssValue("border") + ";" +
                product.getCssValue("opacity") + ";" +
                product.getCssValue("box-shadow");
    }

    public String getProductStyleAfterHover(WebElement product) {
        hoverOverProduct(product);
        try { Thread.sleep(500); } catch (InterruptedException e) {}
        return product.getCssValue("border") + ";" +
                product.getCssValue("opacity") + ";" +
                product.getCssValue("box-shadow");
    }

    public boolean hasHoverEffect(WebElement product) {
        String before = getProductStyleBeforeHover(product);
        String after = getProductStyleAfterHover(product);
        return !before.equals(after);
    }

    public List<WebElement> getSaleProducts() {
        return driver.findElements(specialPrices);
    }

    public boolean hasMultiplePrices(WebElement product) {
        try {
            int oldPriceCount = product.findElements(oldPrices).size();
            int specialPriceCount = product.findElements(specialPrices).size();
            return oldPriceCount > 0 && specialPriceCount > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPriceStrikethrough(WebElement priceElement) {
        String textDecoration = priceElement.getCssValue("text-decoration");
        System.out.println("Text decoration: " + textDecoration);
        return textDecoration.contains("line-through");
    }

    public String getPriceColor(WebElement priceElement) {
        return priceElement.getCssValue("color");
    }

    public boolean isColorGrey(String color) {
        System.out.println("Checking if color is grey: " + color);

        if (color == null || color.isEmpty()) {
            return false;
        }

        try {
            // Extract RGB values
            color = color.replace("rgba(", "").replace("rgb(", "").replace(")", "");
            String[] rgb = color.split(",");

            if (rgb.length >= 3) {
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());

                System.out.println("RGB values: R=" + r + ", G=" + g + ", B=" + b);

                // Grey colors have similar R, G, B values
                int diffRG = Math.abs(r - g);
                int diffRB = Math.abs(r - b);
                int diffGB = Math.abs(g - b);

                boolean isGrey = diffRG < 30 && diffRB < 30 && diffGB < 30;
                System.out.println("Is grey: " + isGrey);
                return isGrey;
            }
        } catch (Exception e) {
            System.out.println("Error parsing color: " + e.getMessage());
        }

        // Fallback: check for color names
        return color.contains("gray") || color.contains("grey");
    }

    public boolean isColorBlue(String color) {
        System.out.println("Checking if color is blue: " + color);

        if (color == null || color.isEmpty()) {
            return false;
        }

        try {
            // Extract RGB values
            color = color.replace("rgba(", "").replace("rgb(", "").replace(")", "");
            String[] rgb = color.split(",");

            if (rgb.length >= 3) {
                int r = Integer.parseInt(rgb[0].trim());
                int g = Integer.parseInt(rgb[1].trim());
                int b = Integer.parseInt(rgb[2].trim());

                System.out.println("RGB values: R=" + r + ", G=" + g + ", B=" + b);

                // Blue color: B value is significantly higher than R and G
                boolean isBlue = b > r && b > g && b > 100;
                System.out.println("Is blue: " + isBlue);
                return isBlue;
            }
        } catch (Exception e) {
            System.out.println("Error parsing color: " + e.getMessage());
        }

        // Fallback: check for color names
        return color.contains("blue");
    }

    public void addToWishlist(int productIndex) {
        List<WebElement> wishlistButtons = driver.findElements(addToWishlistButtons);
        if (productIndex < wishlistButtons.size()) {
            actions.moveToElement(wishlistButtons.get(productIndex)).click().perform();
        }
    }

    public void addToCart(int productIndex) {
        List<WebElement> cartButtons = driver.findElements(addToCartButtons);
        if (productIndex < cartButtons.size()) {
            cartButtons.get(productIndex).click();
        }
    }

    // Metodë ndihmëse për të marrë old price element direkt
    public List<WebElement> getOldPriceElements() {
        return driver.findElements(oldPrices);
    }

    // Metodë ndihmëse për të marrë special price element direkt
    public List<WebElement> getSpecialPriceElements() {
        return driver.findElements(specialPrices);
    }
}