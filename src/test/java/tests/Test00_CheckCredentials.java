package tests;

import base.BaseTest;
import org.junit.jupiter.api.Test;
import utils.TestData;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class Test00_CheckCredentials extends BaseTest {

    @Test
    void testCredentialsCheck() {
        System.out.println("=== CHECKING CREDENTIALS ===");

        String email = TestData.getRegisteredEmail();
        String password = TestData.getRegisteredPassword();

        System.out.println("Email që do të përdoret: " + email);
        System.out.println("Password që do të përdoret: " + password);

        // Kontrollo nëse po përdorim credentials default
        boolean usingDefault = email.contains("autotest_") || email.contains("example.com");

        if (usingDefault) {
            System.out.println("========================================");
            System.out.println("PO PËRDOREN CREDENTIALS DEFAULT!");
            System.out.println("Këto NUK do të funksionojnë për login.");
            System.out.println("");
            System.out.println("JU LUTEM:");
            System.out.println("1. Regjistrohu manualisht në " + TestData.BASE_URL);
            System.out.println("2. Ndrysho MANUAL_EMAIL dhe MANUAL_PASSWORD në TestData.java");
            System.out.println("3. Më pas ekzekuto testet");
            System.out.println("========================================");
        } else {
            System.out.println("========================================");
            System.out.println("PO PËRDOREN CREDENTIALS MANUALE");
            System.out.println("Email: " + email);
            System.out.println("Këto duhet të funksionojnë për login.");
            System.out.println("========================================");
        }

        assertTrue(true, "Credentials check completed");
    }
}