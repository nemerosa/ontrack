package net.nemerosa.ontrack.security;

public interface EncryptionService {
    String encrypt(String plain);

    String decrypt(String crypted);
}
