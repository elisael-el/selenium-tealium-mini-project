package utils;

public class TestData {

    public static final String BASE_URL = "https://ecommerce.tealiumdemo.com/";

    // Përdor një email që ekziston në sistem
    public static final String VALID_EMAIL = "user_1767826063517@mail.com";
    public static final String VALID_PASSWORD = "Password123";

    public static final String FIRST_NAME = "Test";
    public static final String LAST_NAME = "User";
    public static final String REGISTER_PASSWORD = "Password123";

    public static String randomEmail() {
        return "user_" + System.currentTimeMillis() + "@mail.com";
    }
}