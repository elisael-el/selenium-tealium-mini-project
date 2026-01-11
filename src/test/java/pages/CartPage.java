package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By cartItems = By.cssSelector(".cart-table tbody tr");
    private By itemPrices = By.cssSelector(".cart-price .price");
    private By itemQuantities = By.cssSelector(".input-text.qty");
    private By itemTotals = By.cssSelector(".cart-total .price");
    private By grandTotal = By.cssSelector(".grand-total .price");
    private By updateButtons = By.cssSelector("button.btn-update");
    private By removeButtons = By.cssSelector(".product-cart-remove .btn-remove");
    private By emptyCartMessage = By.cssSelector(".cart-empty");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public int getCartItemCount() {
        return driver.findElements(cartItems).size();
    }

    public double getItemPrice(int index) {
        List<WebElement> prices = driver.findElements(itemPrices);
        if (index < prices.size()) {
            String priceText = prices.get(index).getText().replace("$", "").trim();
            return Double.parseDouble(priceText);
        }
        return 0.0;
    }

    public int getItemQuantity(int index) {
        List<WebElement> quantities = driver.findElements(itemQuantities);
        if (index < quantities.size()) {
            return Integer.parseInt(quantities.get(index).getAttribute("value"));
        }
        return 0;
    }

    public void updateQuantity(int index, int newQuantity) {
        List<WebElement> quantities = driver.findElements(itemQuantities);
        List<WebElement> updateBtns = driver.findElements(updateButtons);

        if (index < quantities.size() && index < updateBtns.size()) {
            quantities.get(index).clear();
            quantities.get(index).sendKeys(String.valueOf(newQuantity));
            updateBtns.get(index).click();

            // Wait for cart to update
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public double getItemTotal(int index) {
        List<WebElement> totals = driver.findElements(itemTotals);
        if (index < totals.size()) {
            String totalText = totals.get(index).getText().replace("$", "").trim();
            return Double.parseDouble(totalText);
        }
        return 0.0;
    }

    public double getGrandTotal() {
        try {
            String totalText = wait.until(ExpectedConditions.visibilityOfElementLocated(grandTotal))
                    .getText().replace("$", "").trim();
            return Double.parseDouble(totalText);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void removeItem(int index) {
        List<WebElement> removeBtns = driver.findElements(removeButtons);
        if (index < removeBtns.size()) {
            removeBtns.get(index).click();

            // Wait for removal
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isCartEmpty() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(emptyCartMessage))
                    .getText().contains("You have no items in your shopping cart");
        } catch (Exception e) {
            return getCartItemCount() == 0;
        }
    }

    public double calculateTotalPrice() {
        double total = 0.0;
        int itemCount = getCartItemCount();

        for (int i = 0; i < itemCount; i++) {
            total += getItemTotal(i);
        }

        return total;
    }
}