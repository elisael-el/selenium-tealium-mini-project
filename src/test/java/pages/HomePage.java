package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    private By account = By.xpath("//span[text()='Account']");
    private By login = By.linkText("Log In");
    private By register = By.linkText("Register");
    private By logout = By.linkText("Log Out");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.actions = new Actions(driver);
    }

    public void clickLogin() {
        WebElement accountBtn = wait.until(ExpectedConditions.elementToBeClickable(account));
        accountBtn.click();

        WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(login));
        loginLink.click();
    }

    public void clickRegister() {
        wait.until(ExpectedConditions.elementToBeClickable(account)).click();
        wait.until(ExpectedConditions.elementToBeClickable(register)).click();
    }

    public boolean isLoggedIn() {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(account)).click();
            Thread.sleep(500);
            return driver.findElements(logout).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public void goToWomen() {
        driver.get("https://ecommerce.tealiumdemo.com/women.html");
        wait.until(ExpectedConditions.urlContains("women"));
        System.out.println("Successfully navigated to Women section");
    }

    public void goToMen() {
        driver.get("https://ecommerce.tealiumdemo.com/men.html");
        wait.until(ExpectedConditions.urlContains("men"));
        System.out.println("Successfully navigated to Men section");
    }

    public void goToSale() {
        driver.get("https://ecommerce.tealiumdemo.com/sale.html");
        wait.until(ExpectedConditions.urlContains("sale"));
        System.out.println("Successfully navigated to Sale section");
    }
}