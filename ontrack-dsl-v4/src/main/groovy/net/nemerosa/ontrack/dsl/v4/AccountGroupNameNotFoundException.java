package net.nemerosa.ontrack.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.http.OTNotFoundException;

public class AccountGroupNameNotFoundException extends OTNotFoundException {
    public AccountGroupNameNotFoundException(String group) {
        super("Account group name not found: " + group);
    }
}
