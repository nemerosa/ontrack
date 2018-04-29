package net.nemerosa.ontrack.extension.ldap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import javax.naming.ldap.LdapName;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurableUserDetailsContextMapper extends LdapUserDetailsMapper {

    private final LDAPSettings settings;

    public ConfigurableUserDetailsContextMapper(LDAPSettings settings) {
        this.settings = settings;
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        // Default details
        LdapUserDetails userDetails = (LdapUserDetails) super.mapUserFromContext(ctx, username, authorities);
        return extendUserDetails(ctx, userDetails);

    }

    protected UserDetails extendUserDetails(DirContextOperations ctx, LdapUserDetails userDetails) {
        // Full name
        String fullNameAttribute = settings.getFullNameAttribute();
        if (StringUtils.isBlank(fullNameAttribute)) {
            fullNameAttribute = "cn";
        }
        String fullName = ctx.getStringAttribute(fullNameAttribute);
        // Email
        String emailAttribute = settings.getEmailAttribute();
        if (StringUtils.isBlank(emailAttribute)) {
            emailAttribute = "email";
        }
        String email = ctx.getStringAttribute(emailAttribute);
        // Groups
        String groupAttribute = settings.getGroupAttribute();
        if (StringUtils.isBlank(groupAttribute)) {
            groupAttribute = "memberOf";
        }
        String groupFilter = settings.getGroupFilter();
        String[] groups = ctx.getStringAttributes(groupAttribute);
        Set<String> parsedGroups;
        if (groups != null && groups.length > 0) {
            parsedGroups = Arrays.stream(groups)
                    // Parsing of the group
                    .map(LdapUtils::newLdapName)
                    // Filter on OU
                    .filter(dn -> {
                        String ou = getValue(dn, "OU");
                        return StringUtils.isBlank(groupFilter) || StringUtils.equalsIgnoreCase(ou, groupFilter);
                    })
                    // Getting the common name
                    .map(dn -> getValue(dn, "CN"))
                    // Keeps only the groups being filled in
                    .filter(StringUtils::isNotBlank)
                    // As a set
                    .collect(Collectors.toSet());
        } else {
            parsedGroups = Collections.emptySet();
        }
        // OK
        return new ExtendedLDAPUserDetails(userDetails, fullName, email, parsedGroups);
    }

    protected static String getValue(LdapName dn, String key) {
        try {
            return LdapUtils.getStringValue(dn, StringUtils.upperCase(key));
        } catch (IllegalArgumentException | NoSuchElementException ignored) {
            try {
                return LdapUtils.getStringValue(dn, StringUtils.lowerCase(key));
            } catch (IllegalArgumentException | NoSuchElementException ignored2) {
                return null;
            }
        }
    }
}
