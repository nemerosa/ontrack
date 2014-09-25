package net.nemerosa.ontrack.security;

import javax.crypto.Cipher;

public interface ConfidentialKey {

    /**
     * Name of the key. This is used as the file name.
     */
    String getId();

    Cipher encrypt();

    Cipher decrypt();

    String encrypt(String plain);

    String decrypt(String crypted);
}
