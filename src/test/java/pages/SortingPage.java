package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By sortDropdown = By.cssSelector("select.sorter");
    private By products = By.cssSelector(".products-grid li.item");
    private By productPrices = By.cssSelector(".price");

    public SortingPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void sortBy(String criteria) {
        WebElement sortSelect = wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        Select select = new Select(sortSelect);
        select.selectByVisibleText(criteria);

        // Wait for products to reload
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
    }

    public List<Double> getProductPrices() {
        List<Double> prices = new ArrayList<>();
        List<WebElement> priceElements = driver.findElements(productPrices);

        for (WebElement priceElement : priceElements) {
            try {
                String priceText = priceElement.getText().replace("$", "").trim();
                if (!priceText.isEmpty()) {
                    prices.add(Double.parseDouble(priceText));
                }
            } catch (Exception e) {
                // Skip if price cannot be parsed
            }
        }

        return prices;
    }

    public boolean arePricesSortedAscending() {
        List<Double> prices = getProductPrices();
        if (prices.size() < 2) return true;

        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) < prices.get(i - 1)) {
                return false;
            }
        }
        return true;
    }

    public boolean arePricesSortedDescending() {
        List<Double> prices = getProductPrices();
        if (prices.size() < 2) return true;

        for (int i = 1; i < prices.size(); i++) {
            if (prices.get(i) > prices.get(i - 1)) {
                return false;
            }
        }
        return true;
    }
}