package net.nemerosa.ontrack.model.exceptions;

import net.nemerosa.ontrack.common.BaseException;

public class AuthenticationSourceProviderNotFoundException extends BaseException {

    public AuthenticationSourceProviderNotFoundException(String id) {
        super("Authentication source with ID %s cannot be found.", id);
    }
}
