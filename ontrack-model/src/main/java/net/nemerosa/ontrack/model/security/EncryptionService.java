package net.nemerosa.ontrack.model.security;

public interface EncryptionService {
    String encrypt(String plain);

    String decrypt(String crypted);
}
