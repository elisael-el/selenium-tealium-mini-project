package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CartPage {

    private WebDriver driver;

    private By grandTotal = By.cssSelector(".grand-total .price");
    private By cartItems = By.cssSelector("tbody tr");
    private By emptyCartMessage = By.cssSelector(".cart-empty");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean cartTotalIsDisplayed() {
        try {
            return driver.findElement(grandTotal).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getGrandTotal() {
        return driver.findElement(grandTotal).getText();
    }

    public int getCartItemsCount() {
        return driver.findElements(cartItems).size();
    }

    public boolean isCartEmpty() {
        try {
            return driver.findElement(emptyCartMessage).isDisplayed();
        } catch (Exception e) {
            String pageSource = driver.getPageSource().toLowerCase();
            return pageSource.contains("no items in your shopping cart") ||
                    pageSource.contains("shopping cart is empty");
        }
    }
}