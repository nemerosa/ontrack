package net.nemerosa.ontrack.extension.support.configurations;

public interface EncryptionService {
    String encrypt(String plain);

    String decrypt(String crypted);
}
