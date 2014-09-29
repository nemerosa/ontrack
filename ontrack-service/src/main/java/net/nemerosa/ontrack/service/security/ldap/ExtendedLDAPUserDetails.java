package net.nemerosa.ontrack.service.security.ldap;

import lombok.Data;
import lombok.experimental.Delegate;
import org.springframework.security.ldap.userdetails.LdapUserDetails;

@Data
public class ExtendedLDAPUserDetails implements LdapUserDetails {

    @Delegate
    private final LdapUserDetails support;
    private final String fullName;
    private final String email;

}
