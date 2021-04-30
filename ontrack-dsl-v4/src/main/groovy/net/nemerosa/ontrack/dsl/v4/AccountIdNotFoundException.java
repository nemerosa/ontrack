package net.nemerosa.ontrack.dsl.v4;

import net.nemerosa.ontrack.dsl.v4.http.OTNotFoundException;

public class AccountIdNotFoundException extends OTNotFoundException {
    public AccountIdNotFoundException(int id) {
        super("Account ID not found: " + id);
    }
}
