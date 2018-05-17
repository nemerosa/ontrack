package net.nemerosa.ontrack.extension.ldap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsMapper;

import javax.naming.ldap.LdapName;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigurableUserDetailsContextMapper extends LdapUserDetailsMapper {

    private final LDAPSettings settings;
    private final SpringSecurityLdapTemplate ldapTemplate;

    public ConfigurableUserDetailsContextMapper(LDAPSettings settings, SpringSecurityLdapTemplate ldapTemplate) {
        this.settings = settings;
        this.ldapTemplate = ldapTemplate;
    }

    ConfigurableUserDetailsContextMapper(LDAPSettings settings, ContextSource contextSource) {
        this(
                settings,
                new SpringSecurityLdapTemplate(contextSource)
        );
    }

    @Override
    public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
        // Default details
        LdapUserDetails userDetails = (LdapUserDetails) super.mapUserFromContext(ctx, username, authorities);
        return extendUserDetails(ctx, userDetails, username);
    }

    protected UserDetails extendUserDetails(DirContextOperations ctx, LdapUserDetails userDetails, String username) {
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
        Set<String> parsedGroups = new HashSet<>();
        // ... from the user
        parsedGroups.addAll(getGroupsFromUser(ctx));
        // ... from the groups
        parsedGroups.addAll(getGroups(username));
        // OK
        return new ExtendedLDAPUserDetails(userDetails, fullName, email, parsedGroups);
    }

    private Collection<String> getGroups(String username) {
        String groupSearchBase = settings.getGroupSearchBase();
        if (StringUtils.isNotBlank(groupSearchBase)) {
            String groupSearchFilter = settings.getGroupSearchFilter();
            if (StringUtils.isBlank(groupSearchFilter)) {
                groupSearchFilter = "(member={0})";
            }
            String groupNameAttribute = settings.getGroupNameAttribute();
            if (StringUtils.isBlank(groupNameAttribute)) {
                groupNameAttribute = "cn";
            }
            return ldapTemplate.searchForSingleAttributeValues(
                    groupSearchBase,
                    groupSearchFilter,
                    new String[]{username},
                    groupNameAttribute

            );
        } else {
            return Collections.emptySet();
        }
    }

    private Collection<String> getGroupsFromUser(DirContextOperations ctx) {
        String groupNameAttribute;
        String groupNameAttributeValue = settings.getGroupNameAttribute();
        if (StringUtils.isBlank(groupNameAttributeValue)) {
            groupNameAttribute = "cn";
        } else {
            groupNameAttribute = groupNameAttributeValue;
        }
        String groupAttribute = settings.getGroupAttribute();
        if (StringUtils.isBlank(groupAttribute)) {
            groupAttribute = "memberOf";
        }
        String groupFilter = settings.getGroupFilter();
        String[] groups = ctx.getStringAttributes(groupAttribute);
        if (groups != null && groups.length > 0) {
            return Arrays.stream(groups)
                    // Parsing of the group
                    .map(LdapUtils::newLdapName)
                    // Filter on OU
                    .filter(dn -> {
                        String ou = getValue(dn, "OU");
                        return StringUtils.isBlank(groupFilter) || StringUtils.equalsIgnoreCase(ou, groupFilter);
                    })
                    // Getting the common name
                    .map(dn -> getValue(dn, groupNameAttribute))
                    // Keeps only the groups being filled in
                    .filter(StringUtils::isNotBlank)
                    // As a set
                    .collect(Collectors.toSet());
        } else {
            return Collections.emptySet();
        }
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
