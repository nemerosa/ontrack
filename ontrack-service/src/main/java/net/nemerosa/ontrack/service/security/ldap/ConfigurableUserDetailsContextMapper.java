package net.nemerosa.ontrack.service.security.ldap;

import net.nemerosa.ontrack.model.settings.LDAPSettings;
import org.apache.commons.lang.StringUtils;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import java.util.Collection;

public class ConfigurableUserDetailsContextMapper extends LdapUserDetailsMapper {

    private final LDAPSettings settings;

    public ConfigurableUserDetailsContextMapper(LDAPSettings settings) {
        this.settings = settings;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        // Default details
        LdapUserDetails userDetails = (LdapUserDetails) super.mapUserFromContext(ctx, username, authorities);
        // Full name
        String fullName = username;
        String fullNameAttribute = settings.getFullNameAttribute();
        if (StringUtils.isNotBlank(fullNameAttribute)) {
            fullName = ctx.getStringAttribute(fullNameAttribute);
        }
        // Email
        String email = "";
        String emailAttribute = settings.getEmailAttribute();
        if (StringUtils.isNotBlank(emailAttribute)) {
            email = ctx.getStringAttribute(emailAttribute);
        }
        // OK
        return new ExtendedLDAPUserDetails(userDetails, fullName, email);
    }
}
