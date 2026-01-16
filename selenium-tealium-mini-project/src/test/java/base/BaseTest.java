package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ScreenshotExtension;

import java.time.Duration;

/**
 * Base Test class for all test cases
 * Contains setup, teardown, and common utility methods
 */
public class BaseTest {
    protected WebDriver driver;
    protected JavascriptExecutor js;
    protected WebDriverWait wait;
    protected Actions actions;

    @RegisterExtension
    ScreenshotExtension screenshotExtension = new ScreenshotExtension();

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        // Performance optimizations
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);
        options.setUnhandledPromptBehaviour(org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT);

        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        actions = new Actions(driver);

        // Set timeouts
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        // Navigate to base URL
        driver.get(utils.TestData.BASE_URL);
        System.out.println("Faqja u hap me sukses: " + utils.TestData.BASE_URL);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            }
        }
    }

    /**
     * Get WebDriver instance
     * @return WebDriver instance
     */
    public WebDriver getDriver() {
        return driver;
    }

    /**
     * Get JavaScriptExecutor instance
     * @return JavaScriptExecutor instance
     */
    public JavascriptExecutor getJsExecutor() {
        return js;
    }

    /**
     * Get WebDriverWait instance
     * @return WebDriverWait instance
     */
    public WebDriverWait getWait() {
        return wait;
    }

    /**
     * Get Actions instance
     * @return Actions instance
     */
    public Actions getActions() {
        return actions;
    }

    /**
     * Safe click method that handles ElementClickInterceptedException
     * Tries multiple strategies: regular click, JavaScript click, Actions click
     * @param element WebElement to click
     */
    public void safeClick(WebElement element) {
        try {
            // Strategy 1: Wait for element to be clickable
            wait.until(ExpectedConditions.elementToBeClickable(element));
            element.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            // Strategy 2: JavaScript click
            System.out.println("Regular click intercepted, using JavaScript click");
            js.executeScript("arguments[0].click();", element);
        } catch (Exception e) {
            // Strategy 3: Actions click
            System.out.println("JavaScript click failed, using Actions click");
            actions.moveToElement(element).click().perform();
        }
    }

    /**
     * Safe click method with By locator
     * @param locator By locator to find element
     */
    public void safeClick(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        safeClick(element);
    }

    /**
     * Scroll to element using JavaScript
     * @param element WebElement to scroll to
     */
    public void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block: 'center', behavior: 'smooth'});", element);
        // Small wait for smooth scroll
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    /**
     * Scroll to element and click
     * @param element WebElement to scroll to and click
     */
    public void scrollAndClick(WebElement element) {
        scrollToElement(element);
        safeClick(element);
    }

    /**
     * Wait for element to be visible
     * @param locator By locator
     * @return WebElement once visible
     */
    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be clickable
     * @param locator By locator
     * @return WebElement once clickable
     */
    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for URL to contain text
     * @param urlPart URL part to wait for
     */
    public void waitForUrl(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }
}