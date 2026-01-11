package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // Locators
    private By pageTitle = By.cssSelector("h1");
    private By firstNameField = By.id("firstname");
    private By lastNameField = By.id("lastname");
    private By emailField = By.id("email_address");
    private By passwordField = By.id("password");
    private By confirmPasswordField = By.id("confirmation");
    private By registerButton = By.cssSelector("button[title='Register']");
    private By successMessage = By.cssSelector(".success-msg");
    private By errorMessage = By.cssSelector(".error-msg");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle)).getText();
    }

    public void fillRegistrationForm(String firstName, String lastName, String email, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstNameField)).sendKeys(firstName);
        driver.findElement(lastNameField).sendKeys(lastName);
        driver.findElement(emailField).sendKeys(email);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(confirmPasswordField).sendKeys(password);
    }

    public void submitRegistration() {
        WebElement registerBtn = wait.until(ExpectedConditions.elementToBeClickable(registerButton));
        registerBtn.click();
    }

    public boolean isSuccessMessageDisplayed() {
        try {
            WebElement successMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage));
            return successMsg.getText().contains("Thank you for registering");
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessMessage() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(successMessage)).getText();
        } catch (Exception e) {
            return "";
        }
    }
}