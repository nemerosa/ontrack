package net.nemerosa.ontrack.extension.support.configurations;

import org.springframework.stereotype.Component;

/**
 * FIXME #77 No encryption is provided for the moment by ontrack.
 * <p>
 * Replace this dummy implementation by a real one.
 * <p>
 * This class has probably to be moved to the <code>ontrack-service</code> module.
 */
@Component
public class StupidEncryptionServiceToReplace implements EncryptionService {
    @Override
    public String encrypt(String plain) {
        return "xxx" + plain;
    }

    @Override
    public String decrypt(String crypted) {
        return crypted.substring(3);
    }
}
