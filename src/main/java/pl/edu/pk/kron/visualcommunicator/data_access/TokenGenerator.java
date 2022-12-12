package pl.edu.pk.kron.visualcommunicator.data_access;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGenerator {
    public static String getNextAuthToken() {
        var bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);

        return Base64.getEncoder().encodeToString(bytes);
    }
}
