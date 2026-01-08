package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private By pageTitle = By.cssSelector("h1");
    private By firstName = By.id("firstname");
    private By lastName = By.id("lastname");
    private By email = By.id("email_address");
    private By password = By.id("password");
    private By confirmPassword = By.id("confirmation");
    private By registerBtn = By.xpath("//button[@title='Register']");
    private By successMsg = By.cssSelector("li.success-msg span");

    public RegisterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    public String getPageTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle))
                .getText();
    }

    public void fillForm(String fName, String lName, String mail, String pass) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(firstName)).sendKeys(fName);
        driver.findElement(lastName).sendKeys(lName);
        driver.findElement(email).sendKeys(mail);
        driver.findElement(password).sendKeys(pass);
        driver.findElement(confirmPassword).sendKeys(pass);
    }

    public void submit() {
        wait.until(ExpectedConditions.presenceOfElementLocated(registerBtn));

        var button = driver.findElement(registerBtn);

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView(true);", button);

        ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("arguments[0].click();", button);
    }

    public boolean isSuccessMessageDisplayed() {
        String text = wait.until(
                ExpectedConditions.visibilityOfElementLocated(successMsg)
        ).getText();

        return text.contains("Thank you");
    }
}