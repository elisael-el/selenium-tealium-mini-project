package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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

    private By products = By.cssSelector(".products-grid .item");
    private By productPrices = By.cssSelector(".products-grid .item .price");
    private By blackColorFilter = By.cssSelector("a[style*='background: #000000'], a[title='Black']");
    private By priceDropdown = By.cssSelector("select[title='Price']");

    public FiltersPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void clickBlackColor() {
        try {
            WebElement blackColor = wait.until(ExpectedConditions.elementToBeClickable(blackColorFilter));

            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'});",
                    blackColor
            );

            Thread.sleep(500);

            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", blackColor);
            System.out.println("Clicked black color filter");

            // Wait for filter to apply
            Thread.sleep(3000);

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
            System.out.println("Products reloaded after color filter");

        } catch (Exception e) {
            System.err.println("Error clicking black color: " + e.getMessage());
        }
    }

    public void selectPriceRange(String range) {
        try {
            WebElement dropdown = wait.until(ExpectedConditions.presenceOfElementLocated(priceDropdown));

            Select select = new Select(dropdown);
            select.selectByVisibleText(range);
            System.out.println("Selected price range: " + range);

            // Wait for filter to apply
            Thread.sleep(3000);

            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
            System.out.println("Products reloaded after price filter");

        } catch (Exception e) {
            System.err.println("Error selecting price range: " + e.getMessage());
        }
    }

    public int getProductCount() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
            int count = driver.findElements(products).size();
            System.out.println("Product count: " + count);
            return count;
        } catch (Exception e) {
            System.err.println("Error getting product count: " + e.getMessage());
            return 0;
        }
    }

    public List<WebElement> getProducts() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
        return driver.findElements(products);
    }

    public double getProductPrice(WebElement product) {
        try {
            String priceText = product.findElement(By.cssSelector(".price")).getText();
            return Double.parseDouble(priceText.replace("$", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public boolean isProductColorBorderedBlue(WebElement product) {
        try {
            WebElement colorSwatch = product.findElement(By.cssSelector(".swatch-link"));
            String border = colorSwatch.getCssValue("border");
            String borderColor = colorSwatch.getCssValue("border-color");

            System.out.println("Border: " + border);
            System.out.println("Border color: " + borderColor);

            if (borderColor.contains("rgb")) {
                String[] parts = borderColor.replace("rgba(", "").replace("rgb(", "")
                        .replace(")", "").split(",");
                int r = Integer.parseInt(parts[0].trim());
                int g = Integer.parseInt(parts[1].trim());
                int b = Integer.parseInt(parts[2].trim());

                return b > 150 && b > r && b > g;
            }

            return borderColor.contains("blue");
        } catch (Exception e) {
            System.err.println("Could not check color border: " + e.getMessage());
            return false;
        }
    }
}