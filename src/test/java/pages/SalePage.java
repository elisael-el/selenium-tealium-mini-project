package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class SalePage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By saleProducts = By.cssSelector(".products-grid .item");
    private By oldPrices = By.cssSelector(".old-price .price");
    private By specialPrices = By.cssSelector(".special-price .price");

    public SalePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public List<WebElement> getSaleProducts() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(saleProducts));
        return driver.findElements(saleProducts);
    }

    public List<WebElement> getOldPrices() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(oldPrices));
        return driver.findElements(oldPrices);
    }

    public List<WebElement> getSpecialPrices() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(specialPrices));
        return driver.findElements(specialPrices);
    }

    public boolean isOldPriceStrikethrough(WebElement oldPrice) {
        String textDecoration = oldPrice.getCssValue("text-decoration");
        System.out.println("Text decoration: " + textDecoration);
        return textDecoration.contains("line-through");
    }

    public boolean isOldPriceGrey(WebElement oldPrice) {
        String color = oldPrice.getCssValue("color");
        System.out.println("Old price color: " + color);

        // Grey colors can be:
        // rgba(119, 119, 119, 1) or rgba(128, 128, 128, 1) or rgba(160, 160, 160, 1)
        // or rgb(119, 119, 119) etc.
        // Check if all RGB values are similar (difference <= 50) and >= 100
        if (color.contains("rgba") || color.contains("rgb")) {
            String[] parts = color.replace("rgba(", "").replace("rgb(", "")
                    .replace(")", "").split(",");
            try {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                // Grey: all values similar and in grey range (100-200)
                int maxDiff = Math.max(Math.abs(r - g), Math.abs(r - b));
                maxDiff = Math.max(maxDiff, Math.abs(g - b));

                boolean isSimilar = maxDiff <= 50;
                boolean isGreyRange = r >= 100 && r <= 200 && g >= 100 && g <= 200 && b >= 100 && b <= 200;

                return isSimilar && isGreyRange;
            } catch (Exception e) {
                return false;
            }
        }

        return color.contains("gray") || color.contains("grey");
    }

    public boolean isSpecialPriceBlue(WebElement specialPrice) {
        String color = specialPrice.getCssValue("color");
        System.out.println("Special price color: " + color);

        // Blue colors: rgb(0, 107, 180) or rgb(31, 113, 183) or similar
        // Check if blue component is significantly higher than red and green
        if (color.contains("rgba") || color.contains("rgb")) {
            String[] parts = color.replace("rgba(", "").replace("rgb(", "")
                    .replace(")", "").split(",");
            try {
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                // Blue: b > 150 and b > r and b > g
                return b > 150 && b > r && b > g;
            } catch (Exception e) {
                return false;
            }
        }

        return color.contains("blue");
    }

    public boolean isSpecialPriceNotStrikethrough(WebElement specialPrice) {
        String textDecoration = specialPrice.getCssValue("text-decoration");
        System.out.println("Special price text decoration: " + textDecoration);
        return !textDecoration.contains("line-through");
    }
}