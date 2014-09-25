package net.nemerosa.ontrack.security;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Converts a string into 128-bit AES key.
     */
    public static SecretKey toAes128Key(String s) {
        try {
            // turn secretKey into 256 bit hash
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();
            digest.update(s.getBytes("UTF-8"));

            // Due to the stupid US export restriction JDK only ships 128bit version.
            return new SecretKeySpec(digest.digest(), 0, 128 / 8, "AES");
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}
