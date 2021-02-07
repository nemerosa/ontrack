package net.nemerosa.ontrack.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.http.OTNotFoundException;

public class AccountNameNotFoundException extends OTNotFoundException {
    public AccountNameNotFoundException(String name) {
        super("Account name not found: " + name);
    }
}
