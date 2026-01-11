package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FiltersPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By colorFilters = By.cssSelector(".filter-options .color a");
    private By priceFilter = By.cssSelector("select[title='Price']");
    private By products = By.cssSelector(".products-grid li.item");
    private By colorSwatches = By.cssSelector(".swatch-link");

    public FiltersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void selectColorFilter(String colorName) {
        List<WebElement> colorElements = driver.findElements(colorFilters);
        for (WebElement color : colorElements) {
            if (color.getAttribute("title").contains(colorName)) {
                color.click();
                waitForProductsToReload();
                break;
            }
        }
    }

    public void selectPriceRange(String range) {
        WebElement priceDropdown = wait.until(ExpectedConditions.elementToBeClickable(priceFilter));
        Select select = new Select(priceDropdown);
        select.selectByVisibleText(range);
        waitForProductsToReload();
    }

    private void waitForProductsToReload() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
    }

    public List<WebElement> getFilteredProducts() {
        return driver.findElements(products);
    }

    public boolean isColorSelected(WebElement product, String color) {
        try {
            WebElement swatch = product.findElement(colorSwatches);
            String style = swatch.getCssValue("border");
            String borderColor = swatch.getCssValue("border-color");

            // Check if border is blue
            return borderColor.contains("blue") ||
                    borderColor.contains("rgb(0, 0, 255)") ||
                    borderColor.contains("rgb(0,0,255)");
        } catch (Exception e) {
            return false;
        }
    }

    public double getProductPrice(WebElement product) {
        try {
            WebElement priceElement = product.findElement(By.cssSelector(".price"));
            String priceText = priceElement.getText().replace("$", "").trim();
            return Double.parseDouble(priceText);
        } catch (Exception e) {
            return 0.0;
        }
    }
}