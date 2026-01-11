package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class WishlistPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By wishlistItems = By.cssSelector(".my-wishlist tbody tr");
    private By wishlistCount = By.cssSelector(".block-wishlist .count");
    private By addToCartButtons = By.cssSelector("button.btn-cart");
    private By colorSelects = By.cssSelector("select[title*='Color']");
    private By sizeSelects = By.cssSelector("select[title*='Size']");

    public WishlistPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public int getWishlistItemCount() {
        List<WebElement> items = driver.findElements(wishlistItems);
        return items.size();
    }

    public String getWishlistCountText() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(wishlistCount)).getText();
        } catch (Exception e) {
            return "0";
        }
    }

    public void addAllItemsToCart() {
        List<WebElement> items = driver.findElements(wishlistItems);

        for (int i = 0; i < items.size(); i++) {
            List<WebElement> currentItems = driver.findElements(wishlistItems);
            WebElement item = currentItems.get(i);

            // Select color if available
            List<WebElement> colorOptions = item.findElements(colorSelects);
            if (!colorOptions.isEmpty()) {
                Select colorSelect = new Select(colorOptions.get(0));
                if (colorSelect.getOptions().size() > 1) {
                    colorSelect.selectByIndex(1);
                }
            }

            // Select size if available
            List<WebElement> sizeOptions = item.findElements(sizeSelects);
            if (!sizeOptions.isEmpty()) {
                Select sizeSelect = new Select(sizeOptions.get(0));
                if (sizeSelect.getOptions().size() > 1) {
                    sizeSelect.selectByIndex(1);
                }
            }

            // Click add to cart
            WebElement addToCartBtn = item.findElement(addToCartButtons);
            addToCartBtn.click();

            // Wait for cart to update
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}