package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for Login Page
 * Contains methods to perform login operations
 */
public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    // Locators
    private By emailField = By.id("email");
    private By passwordField = By.id("pass");
    private By loginButton = By.id("send2");
    private By welcomeMessage = By.cssSelector(".welcome-msg .hello strong");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.js = (JavascriptExecutor) driver;
    }

    /**
     * Perform login with email and password
     * Uses multiple strategies to handle click interception
     * @param email User email
     * @param password User password
     */
    public void login(String email, String password) {
        // Wait for and fill email field
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        emailInput.clear();
        emailInput.sendKeys(email);

        // Wait for and fill password field
        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        passwordInput.clear();
        passwordInput.sendKeys(password);

        // Get login button
        WebElement loginBtn = wait.until(ExpectedConditions.presenceOfElementLocated(loginButton));

        // Scroll to button to ensure it's visible
        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", loginBtn);

        // Small wait for any overlays to disappear
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignore
        }

        // Click with fallback strategies
        boolean clicked = false;

        // Strategy 1: Wait for clickable and regular click
        try {
            wait.until(ExpectedConditions.elementToBeClickable(loginButton));
            loginBtn.click();
            clicked = true;
            System.out.println("Login button clicked successfully (regular click)");
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            System.out.println("Regular click intercepted, trying JavaScript click...");
        }

        // Strategy 2: JavaScript click
        if (!clicked) {
            try {
                js.executeScript("arguments[0].click();", loginBtn);
                clicked = true;
                System.out.println("Login button clicked successfully (JavaScript)");
            } catch (Exception e) {
                System.out.println("JavaScript click failed, trying Actions...");
            }
        }

        // Strategy 3: Actions click
        if (!clicked) {
            try {
                new org.openqa.selenium.interactions.Actions(driver)
                        .moveToElement(loginBtn)
                        .pause(Duration.ofMillis(300))
                        .click()
                        .perform();
                clicked = true;
                System.out.println("Login button clicked successfully (Actions)");
            } catch (Exception e) {
                System.err.println("All click strategies failed!");
                throw e;
            }
        }

        // Wait for redirect away from login page
        wait.until(ExpectedConditions.not(
                ExpectedConditions.urlContains("login")
        ));

        System.out.println("Successfully redirected after login");
    }

    /**
     * Get welcome message after login
     * @return Welcome message text
     */
    public String getWelcomeMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(welcomeMessage))
                    .getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Check if user is logged in by verifying URL or welcome message
     * @return true if logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        try {
            // Check if we're on account page
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("customer/account") && !currentUrl.contains("login")) {
                return true;
            }

            // Check for welcome message
            return !getWelcomeMessage().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}