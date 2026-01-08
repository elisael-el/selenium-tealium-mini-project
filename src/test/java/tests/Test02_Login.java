package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.LoginPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test02_Login extends BaseTest {

    @Test
    void loginTest() {
        HomePage home = new HomePage(driver);
        LoginPage login = new LoginPage(driver);

        // 1. Kliko Login
        home.clickLogin();

        // 2. Bëj login
        login.login(TestData.VALID_EMAIL, TestData.VALID_PASSWORD);

        // 3. Verifiko login
        assertTrue(home.isLoggedIn(), "User should be logged in successfully");
    }
}