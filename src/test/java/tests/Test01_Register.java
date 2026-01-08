package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import pages.HomePage;
import pages.RegisterPage;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test01_Register extends BaseTest {

    // Në Test01_Register.java
    @Test
    void registerUserSuccessfully() {
        HomePage home = new HomePage(driver);
        RegisterPage register = new RegisterPage(driver);

        String email = TestData.randomEmail();
        System.out.println("Created user with email: " + email); // Kopjo këtë email

        home.clickRegister();
        register.fillForm(TestData.FIRST_NAME, TestData.LAST_NAME, email, TestData.REGISTER_PASSWORD);
        register.submit();
        assertTrue(register.isSuccessMessageDisplayed(), "User should be registered successfully");
    }
}
