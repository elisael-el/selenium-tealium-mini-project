package utils;

import base.BaseTest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScreenshotExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Object testInstance = context.getRequiredTestInstance();

        if (testInstance instanceof BaseTest) {
            BaseTest baseTest = (BaseTest) testInstance;
            WebDriver driver = baseTest.getDriver();

            if (driver != null) {
                try {
                    takeScreenshot(driver, context.getDisplayName());
                } catch (Exception e) {
                    System.err.println("Failed to take screenshot (driver might be closed): " + e.getMessage());
                }
            }
        }
    }

    private void takeScreenshot(WebDriver driver, String testName) {
        try {
            // Kontrollo nëse driver është ende aktiv
            if (driver.toString().contains("null")) {
                System.out.println("Driver is already closed, skipping screenshot");
                return;
            }

            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String safeTestName = testName.replaceAll("[^a-zA-Z0-9]", "_");

            File directory = new File("screenshots");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File destination = new File(directory, safeTestName + "_" + timestamp + ".png");
            Files.copy(screenshot.toPath(), destination.toPath());

            System.out.println("Screenshot saved: " + destination.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to save screenshot: " + e.getMessage());
        }
    }
}