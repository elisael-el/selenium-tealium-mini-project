package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By emailField = By.id("email");
    private By passwordField = By.id("pass");
    private By loginButton = By.id("send2");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public void login(String mail, String pass) {
        // Plotëso email
        WebElement email = wait.until(ExpectedConditions.visibilityOfElementLocated(emailField));
        email.sendKeys(mail);

        // Plotëso password
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordField));
        password.sendKeys(pass);

        // Kliko login me JavaScript (më i sigurt)
        WebElement loginBtn = wait.until(ExpectedConditions.presenceOfElementLocated(loginButton));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", loginBtn);

        // Prit që të ngarkohet faqja pas login
        wait.until(ExpectedConditions.urlContains("customer/account"));
    }
}