package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By emailField = By.id("email");
    private By passwordField = By.id("pass");
    private By loginButton = By.id("send2");
    private By welcomeMessage = By.cssSelector(".welcome-msg .hello strong");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailField)).sendKeys(email);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();
    }

    public String getWelcomeMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isUserLoggedIn() {
        return !getWelcomeMessage().isEmpty();
    }
}