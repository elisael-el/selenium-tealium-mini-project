package utils;

import base.BaseTest;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.nio.file.Files;

public class ScreenshotExtension implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {

        Object testInstance = context.getRequiredTestInstance();

        if (!(testInstance instanceof BaseTest)) {
            return;
        }

        BaseTest baseTest = (BaseTest) testInstance;
        WebDriver driver = baseTest.getDriver();

        try {
            if (driver == null) {
                return;
            }

            File screenshot = ((TakesScreenshot) driver)
                    .getScreenshotAs(OutputType.FILE);

            File dir = new File("screenshots");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String testName = context.getDisplayName()
                    .replaceAll("[^a-zA-Z0-9]", "_");

            File destination = new File(dir, testName + ".png");
            Files.copy(screenshot.toPath(), destination.toPath());

            System.out.println("Screenshot saved: " + destination.getAbsolutePath());

        } catch (Exception e) {
            System.out.println("Screenshot skipped (browser already closed)");
        }
    }
}
