package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Default encryption service
 */
@Component
public class EncryptionServiceImpl implements EncryptionService {

    private final ConfidentialKey key;

    public EncryptionServiceImpl(ConfidentialKey key) {
        this.key = key;
    }

    @Autowired
    public EncryptionServiceImpl(ConfidentialStore confidentialStore) {
        this(new CryptoConfidentialKey(
                confidentialStore,
                "net.nemerosa.ontrack.security.EncryptionServiceImpl.encryption"
        ));
    }

    @Override
    public String encrypt(String plain) {
        return plain != null ? key.encrypt(plain) : null;
    }

    @Override
    public String decrypt(String crypted) {
        return crypted != null ? key.decrypt(crypted) : null;
    }

    @Override
    public String exportKey() {
        checkAdmin();
        try {
            return key.exportKey();
        } catch (IOException e) {
            throw new EncryptionException(e);
        }
    }

    @Override
    public void importKey(String payload) {
        checkAdmin();
        try {
            key.importKey(payload);
        } catch (IOException e) {
            throw new EncryptionException(e);
        }
    }

    private void checkAdmin() {
        boolean authorised;
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && (authentication.getPrincipal() instanceof OntrackAuthenticatedUser)) {
            OntrackAuthenticatedUser user = (OntrackAuthenticatedUser) authentication.getPrincipal();
            authorised = user.isGranted(ApplicationManagement.class) &&
                    user.isGranted(GlobalSettings.class);
        } else {
            authorised = false;
        }
        // NOT GRANTED
        if (!authorised) {
            throw new AccessDeniedException(
                    "The current used has attempted to import/export keys without being authorised: " +
                            (authentication != null ? authentication.getName() : "anonymous")
            );
        }
    }
}
