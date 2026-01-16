package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.TestData;

import java.time.Duration;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManualLoginTest extends BaseTest {

    @Test
    void manualLoginTest() {
        System.out.println("=== MANUAL LOGIN TEST ===");
        System.out.println("Kjo është për të testuar manualisht login-in");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // Hapi 1: Shko në faqen kryesore
            driver.get(TestData.BASE_URL);
            Thread.sleep(3000);

            // Hapi 2: Kliko Account -> Login
            WebElement accountLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//span[text()='Account']")
            ));
            accountLink.click();
            Thread.sleep(1000);

            WebElement loginLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.linkText("Log In")
            ));
            loginLink.click();
            Thread.sleep(3000);

            // Hapi 3: Jepi përdoruesit mundësinë të vendosë credentialet
            Scanner scanner = new Scanner(System.in);

            System.out.println("=== VENDOS CREDENTIALET ===");
            System.out.print("Email: ");
            String email = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            System.out.println("===========================");

            // Hapi 4: Plotëso formën
            WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("email")
            ));
            emailField.clear();
            emailField.sendKeys(email);

            WebElement passwordField = driver.findElement(By.id("pass"));
            passwordField.clear();
            passwordField.sendKeys(password);

            // Hapi 5: Kliko Login
            WebElement loginButton = driver.findElement(By.id("send2"));
            loginButton.click();
            Thread.sleep(5000);

            // Hapi 6: Kontrollo nëse login-i është i suksesshëm
            String currentUrl = driver.getCurrentUrl();
            String pageSource = driver.getPageSource().toLowerCase();

            boolean loggedIn = currentUrl.contains("customer/account") ||
                    pageSource.contains("my account") ||
                    pageSource.contains("welcome") ||
                    pageSource.contains("logout") ||
                    pageSource.contains("my dashboard");

            if (loggedIn) {
                System.out.println("LOGIN I SUKSESSHËM!");
                System.out.println("Email: " + email);
                System.out.println("Password: " + password);

                // Ruaj credentialet për testet e tjera
                TestData.setRegisteredCredentials(email, password);

                // Logout
                WebElement accountLink2 = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//span[text()='Account']")
                ));
                accountLink2.click();
                Thread.sleep(1000);

                WebElement logoutLink = wait.until(ExpectedConditions.elementToBeClickable(
                        By.linkText("Log Out")
                ));
                logoutLink.click();
                Thread.sleep(2000);

                System.out.println("Credentialet u ruajtën me sukses!");
                assertTrue(true, "Manual login successful");
            } else {
                System.out.println("LOGIN DËSHTOI!");
                System.out.println("Ju lutem kontrolloni credentialet tuaja.");
                assertTrue(false, "Manual login failed");
            }

            scanner.close();

        } catch (Exception e) {
            System.out.println("Gabim: " + e.getMessage());
            e.printStackTrace();
            assertTrue(false, "Test failed with exception");
        }
    }
}