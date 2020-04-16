package net.nemerosa.ontrack.service.security;

import javax.crypto.Cipher;
import java.io.IOException;

public interface ConfidentialKey {

    /**
     * Name of the key. This is used as the file name.
     */
    String getId();

    Cipher encrypt();

    Cipher decrypt();

    String encrypt(String plain);

    String decrypt(String crypted);

    String exportKey() throws IOException;

    void importKey(String key) throws IOException;
}
