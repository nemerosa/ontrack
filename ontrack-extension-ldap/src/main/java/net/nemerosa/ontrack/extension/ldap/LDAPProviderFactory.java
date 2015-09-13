package net.nemerosa.ontrack.extension.ldap;

import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;

public interface LDAPProviderFactory {

    LdapAuthenticationProvider getProvider();

    void invalidate();

}
