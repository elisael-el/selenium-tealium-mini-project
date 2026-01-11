package utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TestData {
    public static final String BASE_URL = "https://ecommerce.tealiumdemo.com/";
    public static final String FIRST_NAME = "Test";
    public static final String LAST_NAME = "User";
    public static final String DEFAULT_PASSWORD = "Test123!";

    private static final String CONFIG_FILE = "test_config.properties";
    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            // Krijoni skedarin nëse nuk ekziston
            saveProperties();
        }
    }

    private static void saveProperties() {
        try {
            properties.store(new FileOutputStream(CONFIG_FILE),
                    "Test Configuration - Auto-generated");
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public static String generateUniqueEmail() {
        String email = "autotest_" + System.currentTimeMillis() + "@test.com";
        properties.setProperty("registered_email", email);
        properties.setProperty("registered_password", DEFAULT_PASSWORD);
        saveProperties();
        return email;
    }

    public static void setRegisteredCredentials(String email, String password) {
        properties.setProperty("registered_email", email);
        properties.setProperty("registered_password", password);
        saveProperties();
        System.out.println("=== CREDENTIALS SAVED ===");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("=========================");
    }

    public static String getRegisteredEmail() {
        // 1. Provo credentialet manuale së pari
        String manualEmail = System.getProperty("manual_email");
        if (manualEmail != null && !manualEmail.isEmpty()) {
            System.out.println("Using SYSTEM property email: " + manualEmail);
            return manualEmail;
        }

        // 2. Provo nga konfigurimi i ruajtur
        String email = properties.getProperty("registered_email");
        if (email != null && !email.isEmpty()) {
            System.out.println("Using SAVED email: " + email);
            return email;
        }

        // 3. Krijo një të re
        return generateUniqueEmail();
    }

    public static String getRegisteredPassword() {
        // 1. Provo password manual së pari
        String manualPassword = System.getProperty("manual_password");
        if (manualPassword != null && !manualPassword.isEmpty()) {
            System.out.println("Using SYSTEM property password");
            return manualPassword;
        }

        // 2. Provo nga konfigurimi i ruajtur
        String password = properties.getProperty("registered_password");
        if (password != null && !password.isEmpty()) {
            System.out.println("Using SAVED password");
            return password;
        }

        // 3. Përdor default
        System.out.println("Using DEFAULT password");
        return DEFAULT_PASSWORD;
    }

    // Për të përdorur në testim pa pasur nevojë për modifikim kod
    public static String getEmailForLogin() {
        String email = getRegisteredEmail();
        // Rikthe nëse është email test
        if (email.contains("@test.com")) {
            return email;
        }
        return email;
    }

    public static String getPasswordForLogin() {
        return getRegisteredPassword();
    }
}