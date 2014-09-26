package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.service.Caches;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.stereotype.Service;

@Service
public class LDAPProviderFactoryImpl implements LDAPProviderFactory {

    /*
    private final SettingsInternalService settingsService;
    private final LdapAuthoritiesPopulator authoritiesPopulator;

    @Autowired
    public LDAPProviderFactoryImpl(SettingsInternalService settingsService, LdapAuthoritiesPopulator authoritiesPopulator) {
        this.settingsService = settingsService;
        this.authoritiesPopulator = authoritiesPopulator;
    }
    */

    @Cacheable(value = Caches.LDAP_SETTINGS, key = "'0'")
    public LdapAuthenticationProvider getProvider() {
        /*
        LDAPConfiguration configuration = adminService.getLDAPConfiguration();
        if (configuration.isEnabled()) {
            // LDAP URL
            String ldapUrl = String.format("ldap://%s:%s", configuration.getHost(), configuration.getPort());
            // LDAP context
            DefaultSpringSecurityContextSource ldapContextSource = new DefaultSpringSecurityContextSource(ldapUrl);
            ldapContextSource.setUserDn(configuration.getUser());
            ldapContextSource.setPassword(configuration.getPassword());
            try {
                ldapContextSource.afterPropertiesSet();
            } catch (Exception e) {
                throw new CannotInitializeLDAPException(e);
            }
            // User search
            FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
                    configuration.getSearchBase(),
                    configuration.getSearchFilter(),
                    ldapContextSource);
            userSearch.setSearchSubtree(true);
            // Bind authenticator
            BindAuthenticator bindAuthenticator = new BindAuthenticator(ldapContextSource);
            bindAuthenticator.setUserSearch(userSearch);
            // Provider
            LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
            ldapAuthenticationProvider.setUserDetailsContextMapper(new ConfigurableUserDetailsContextMapper());
            // OK
            return ldapAuthenticationProvider;
        } else {
            return null;
        }
        */
        return null;
    }

    /**
     private class ConfigurableUserDetailsContextMapper extends LdapUserDetailsMapper {

    @Override public UserDetails mapUserFromContext(DirContextOperations ctx, String username, Collection<? extends GrantedAuthority> authorities) {
    // Gets the configuration
    LDAPConfiguration configuration = adminService.getLDAPConfiguration();
    // Default details
    LdapUserDetails userDetails = (LdapUserDetails) super.mapUserFromContext(ctx, username, authorities);
    // Full name
    String fullName = username;
    String fullNameAttribute = configuration.getFullNameAttribute();
    if (StringUtils.isNotBlank(fullNameAttribute)) {
    fullName = ctx.getStringAttribute(fullNameAttribute);
    }
    // Email
    String email = "";
    String emailAttribute = configuration.getEmailAttribute();
    if (StringUtils.isNotBlank(emailAttribute)) {
    email = ctx.getStringAttribute(emailAttribute);
    }
    // OK
    return new PersonLDAPUserDetails(userDetails, fullName, email);
    }
    }
     */
}
