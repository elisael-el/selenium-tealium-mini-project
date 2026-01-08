package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage {

    private WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    private By products = By.cssSelector(".products-grid .item");
    private By productImages = By.cssSelector(".products-grid .item .product-image");
    private By prices = By.cssSelector(".price");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new Actions(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public List<WebElement> getProducts() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(products));
        List<WebElement> productList = driver.findElements(products);
        System.out.println("Found " + productList.size() + " products");
        return productList;
    }

    public List<WebElement> getProductImages() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productImages));
        return driver.findElements(productImages);
    }

    public void hoverProduct(WebElement product) {
        actions.moveToElement(product).perform();
        System.out.println("Hovered over product");
    }

    public String getProductClass(WebElement product) {
        return product.getAttribute("class");
    }

    // Metoda për të marrë border style (hover effect shpesh ndryshon border)
    public String getProductBorderStyle(WebElement product) {
        return product.getCssValue("border");
    }

    // Metoda për të marrë opacity (hover effect mund të ndryshojë opacity)
    public String getProductOpacity(WebElement product) {
        return product.getCssValue("opacity");
    }

    // Metoda për të marrë box-shadow (hover effect shpesh shton shadow)
    public String getProductBoxShadow(WebElement product) {
        return product.getCssValue("box-shadow");
    }

    public List<Double> getPrices() {
        return driver.findElements(prices)
                .stream()
                .map(e -> e.getText().replace("$", "").trim())
                .filter(t -> !t.isEmpty())
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }
}