package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.common.BaseException;

public class CannotInitializeLDAPException extends BaseException {
    public CannotInitializeLDAPException(Exception e) {
        super("Cannot initialise connection to LDAP: %s", e);
    }
}
