package net.nemerosa.ontrack.extension.ldap;

import lombok.Data;
import lombok.experimental.Delegate;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

import java.util.Collection;

@Data
public class ExtendedLDAPUserDetails implements LdapUserDetails {

    @Delegate
    private final LdapUserDetails support;
    private final String fullName;
    private final String email;
    private final Collection<String> groups;

}
