package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.common.BaseException;

public class EncryptionException extends BaseException {
    public EncryptionException(Exception ex) {
        super(ex, "Problem with encryption: %s", ex);
    }
}
