package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for Home Page
 * Contains methods to interact with main navigation and menus
 */
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

    // Main menu items
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

    /**
     * Click on Account dropdown
     */
    public void clickAccount() {
        WebElement account = wait.until(ExpectedConditions.elementToBeClickable(accountLink));
        account.click();
    }

    /**
     * Click on Register link
     */
    public void clickRegister() {
        clickAccount();
        WebElement register = wait.until(ExpectedConditions.elementToBeClickable(registerLink));
        register.click();
    }

    /**
     * Click on Login link
     */
    public void clickLogin() {
        clickAccount();
        WebElement login = wait.until(ExpectedConditions.elementToBeClickable(loginLink));
        login.click();
    }

    /**
     * Click on Logout link
     */
    public void clickLogout() {
        clickAccount();
        WebElement logout = wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        logout.click();
    }

    /**
     * Check if user is logged in
     * @return true if logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        try {
            clickAccount();
            return driver.findElements(logoutLink).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get welcome message text
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
     * Navigate to Women section
     * Hover over Women menu and click View All Women
     */
    public void navigateToWomenSection() {
        try {
            WebElement women = wait.until(ExpectedConditions.presenceOfElementLocated(womenMenu));
            actions.moveToElement(women).perform();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Ignore
            }

            WebElement viewAll = wait.until(ExpectedConditions.elementToBeClickable(womenViewAll));
            viewAll.click();

        } catch (Exception e) {
            System.out.println("Women menu not found, trying direct URL...");
            String currentUrl = driver.getCurrentUrl();
            String baseUrl = currentUrl.split("/")[0] + "//" + currentUrl.split("/")[2];
            driver.get(baseUrl + "/women.html");
        }

        // Wait for page to load
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("women"),
                ExpectedConditions.titleContains("Women")
        ));
    }

    /**
     * Navigate to Men section
     * Hover over Men menu and click View All Men
     */
    public void navigateToMenSection() {
        try {
            WebElement men = wait.until(ExpectedConditions.presenceOfElementLocated(menMenu));
            actions.moveToElement(men).perform();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Ignore
            }

            WebElement viewAll = wait.until(ExpectedConditions.elementToBeClickable(menViewAll));
            viewAll.click();

        } catch (Exception e) {
            System.out.println("Men menu not found, trying direct URL...");
            String currentUrl = driver.getCurrentUrl();
            String baseUrl = currentUrl.split("/")[0] + "//" + currentUrl.split("/")[2];
            driver.get(baseUrl + "/men.html");
        }

        // Wait for page to load
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("men"),
                ExpectedConditions.titleContains("Men")
        ));
    }

    /**
     * Navigate to Sale section
     * Hover over Sale menu and click View All Sale
     * Uses fallback strategy if Sale menu is not found
     */
    public void navigateToSaleSection() {
        try {
            // Strategy 1: Try to find Sale menu by link text
            WebElement sale = wait.until(ExpectedConditions.presenceOfElementLocated(saleMenu));
            actions.moveToElement(sale).perform();

            // Small wait for submenu to appear
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // Ignore
            }

            WebElement viewAll = wait.until(ExpectedConditions.elementToBeClickable(saleViewAll));
            viewAll.click();

        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("Sale menu not found with standard locator, trying alternatives...");

            // Strategy 2: Try to find Sale in navigation bar
            try {
                List<org.openqa.selenium.WebElement> navLinks = driver.findElements(By.cssSelector("nav a, .nav a, .navigation a"));
                for (org.openqa.selenium.WebElement link : navLinks) {
                    if (link.getText().trim().equalsIgnoreCase("Sale") ||
                            link.getText().trim().contains("SALE")) {
                        actions.moveToElement(link).perform();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ex) {
                            // Ignore
                        }

                        // Try to find View All
                        try {
                            WebElement viewAll = driver.findElement(By.linkText("View All Sale"));
                            viewAll.click();
                            waitForSalePage();
                            return;
                        } catch (Exception ex) {
                            // Click on Sale link directly
                            link.click();
                            waitForSalePage();
                            return;
                        }
                    }
                }
            } catch (Exception ex) {
                System.out.println("Could not find Sale in navigation, trying direct URL...");
            }

            // Strategy 3: Go directly to Sale page URL
            String currentUrl = driver.getCurrentUrl();
            String baseUrl = currentUrl.split("/")[0] + "//" + currentUrl.split("/")[2];
            driver.get(baseUrl + "/sale.html");

            waitForSalePage();
        }
    }

    /**
     * Wait for Sale page to load
     */
    private void waitForSalePage() {
        // Wait for page to load - check URL or title
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("sale"),
                ExpectedConditions.urlContains("Sale"),
                ExpectedConditions.titleContains("Sale"),
                ExpectedConditions.titleContains("sale")
        ));

        System.out.println("Sale page loaded successfully");
    }
}