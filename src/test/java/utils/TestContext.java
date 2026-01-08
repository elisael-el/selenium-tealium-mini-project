package utils;

public class TestContext {

    private static String registeredEmail;
    private static String registeredPassword;

    public static void setRegisteredEmail(String email) {
        registeredEmail = email;
    }

    public static String getRegisteredEmail() {
        return registeredEmail;
    }

    public static void setRegisteredPassword(String password) {
        registeredPassword = password;
    }

    public static String getRegisteredPassword() {
        return registeredPassword;
    }
}
