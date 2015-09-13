package net.nemerosa.ontrack.dsl;

import net.nemerosa.ontrack.dsl.http.OTNotFoundException;

public class AccountGroupNameNotFoundException extends OTNotFoundException {
    public AccountGroupNameNotFoundException(String group) {
        super("Account group name not found: " + group);
    }
}
