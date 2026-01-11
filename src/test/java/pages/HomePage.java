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

    // Locators
    private By accountLink = By.cssSelector(".skip-link.skip-account .label");
    private By registerLink = By.linkText("Register");
    private By loginLink = By.linkText("Log In");
    private By logoutLink = By.linkText("Log Out");
    private By welcomeMessage = By.cssSelector(".welcome-msg");
    private By womenMenu = By.linkText("Women");
    private By womenViewAll = By.linkText("View All Women");
    private By menMenu = By.linkText("Men");
    private By menViewAll = By.linkText("View All Men");
    private By saleMenu = By.linkText("Sale");
    private By saleViewAll = By.linkText("View All Sale");

    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.actions = new Actions(driver);
    }

    public void clickAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(accountLink)).click();
    }

    public void clickRegister() {
        clickAccount();
        wait.until(ExpectedConditions.elementToBeClickable(registerLink)).click();
    }

    public void clickLogin() {
        clickAccount();
        wait.until(ExpectedConditions.elementToBeClickable(loginLink)).click();
    }

    public void clickLogout() {
        clickAccount();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }

    public boolean isUserLoggedIn() {
        try {
            clickAccount();
            return driver.findElements(logoutLink).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public String getWelcomeMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public void navigateToWomenSection() {
        actions.moveToElement(driver.findElement(womenMenu)).perform();
        wait.until(ExpectedConditions.elementToBeClickable(womenViewAll)).click();
    }

    public void navigateToMenSection() {
        actions.moveToElement(driver.findElement(menMenu)).perform();
        wait.until(ExpectedConditions.elementToBeClickable(menViewAll)).click();
    }

    public void navigateToSaleSection() {
        actions.moveToElement(driver.findElement(saleMenu)).perform();
        wait.until(ExpectedConditions.elementToBeClickable(saleViewAll)).click();
    }
}