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

public class WishlistPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By wishlistItems = By.cssSelector(".products-grid .item, tbody tr");
    private By addToCartButtons = By.cssSelector("button[title='Add to Cart']");
    private By colorSelects = By.cssSelector("select[title*='Color']");
    private By sizeSelects = By.cssSelector("select[title*='Size']");

    public WishlistPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public int getWishlistItemsCount() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(wishlistItems));
        return driver.findElements(wishlistItems).size();
    }

    public boolean wishlistHasItems() {
        return getWishlistItemsCount() > 0;
    }

    public void addAllItemsToCart() {
        List<WebElement> items = driver.findElements(wishlistItems);
        System.out.println("Found " + items.size() + " items in wishlist");

        for (int i = 0; i < items.size(); i++) {
            try {
                List<WebElement> currentItems = driver.findElements(wishlistItems);
                WebElement item = currentItems.get(i);

                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView(true);", item
                );

                List<WebElement> colors = item.findElements(colorSelects);
                if (!colors.isEmpty()) {
                    Select colorSelect = new Select(colors.get(0));
                    if (colorSelect.getOptions().size() > 1) {
                        colorSelect.selectByIndex(1);
                    }
                }

                List<WebElement> sizes = item.findElements(sizeSelects);
                if (!sizes.isEmpty()) {
                    Select sizeSelect = new Select(sizes.get(0));
                    if (sizeSelect.getOptions().size() > 1) {
                        sizeSelect.selectByIndex(1);
                    }
                }

                WebElement addToCartBtn = item.findElement(addToCartButtons);
                wait.until(ExpectedConditions.elementToBeClickable(addToCartBtn));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", addToCartBtn);
                System.out.println("Added item " + (i + 1) + " to cart");

                wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.cssSelector("li.success-msg, .success-msg")
                ));

            } catch (Exception e) {
                System.err.println("Error adding item to cart: " + e.getMessage());
            }
        }
    }
}