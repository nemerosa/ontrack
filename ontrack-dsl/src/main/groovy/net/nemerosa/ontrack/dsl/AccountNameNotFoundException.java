package net.nemerosa.ontrack.dsl;

import net.nemerosa.ontrack.dsl.http.OTNotFoundException;

public class AccountNameNotFoundException extends OTNotFoundException {
    public AccountNameNotFoundException(String name) {
        super("Account name not found: " + name);
    }
}
