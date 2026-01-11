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
import utils.ScreenshotExtension;

import java.time.Duration;

public class BaseTest {
    protected WebDriver driver;
    protected JavascriptExecutor js;

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

        // Optimizime për performancë
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);
        options.setUnhandledPromptBehaviour(org.openqa.selenium.UnexpectedAlertBehaviour.ACCEPT);

        driver = new ChromeDriver(options);
        js = (JavascriptExecutor) driver;

        // Rrit timeout-et për faqe të ngadaltë
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

        // Navigate me try-catch për timeout
        try {
            driver.get(utils.TestData.BASE_URL);
            System.out.println("Faqja u hap me sukses: " + utils.TestData.BASE_URL);
        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("Timeout gjatë ngarkimit të faqes, por vazhdojmë: " + e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            try {
                Thread.sleep(1000);
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            }
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    // Metodë helper për të klikuar me JavaScript
    public void jsClick(WebElement element) {
        js.executeScript("arguments[0].click();", element);
    }

    // Metodë helper për të scroll në element
    public void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Getter për JavaScriptExecutor
    public JavascriptExecutor getJsExecutor() {
        return js;
    }
    // Në BaseTest.java
// Metodë helper për të klikuar me JavaScript (më e sigurt)
    public void safeClick(WebElement element) {
        try {
            element.click();
        } catch (org.openqa.selenium.ElementClickInterceptedException e) {
            // Nëse ka interception, përdor JavaScript
            js.executeScript("arguments[0].click();", element);
        }
    }

    // Metodë helper për të klikuar me By selector
    public void safeClick(By locator) {
        WebElement element = driver.findElement(locator);
        safeClick(element);
    }
}