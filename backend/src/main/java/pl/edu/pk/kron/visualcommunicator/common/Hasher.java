package pl.edu.pk.kron.visualcommunicator.common;

import java.nio.charset.StandardCharsets;
import com.google.common.hash.Hashing;

public class Hasher {
    public static String sha256(String original) {
        return Hashing.sha256()
                .hashString(original, StandardCharsets.UTF_8)
                .toString();
    }
}
