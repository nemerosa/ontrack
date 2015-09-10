package net.nemerosa.ontrack.service.security.ldap;

import net.nemerosa.ontrack.common.Caches;
import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.settings.LDAPSettings;
import net.nemerosa.ontrack.service.support.SettingsInternalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

@Deprecated
public class LDAPProviderFactoryImpl implements LDAPProviderFactory {

    private final SettingsInternalService settingsService;
    private final LdapAuthoritiesPopulator authoritiesPopulator = (userData, username) -> AuthorityUtils.createAuthorityList(SecurityRole.USER.name());

    @Autowired
    public LDAPProviderFactoryImpl(SettingsInternalService settingsService) {
        this.settingsService = settingsService;
    }

    @Cacheable(value = Caches.LDAP_SETTINGS, key = "'0'")
    public LdapAuthenticationProvider getProvider() {
        LDAPSettings settings = settingsService.getLDAPSettings();
        if (settings.isEnabled()) {
            // LDAP context
            DefaultSpringSecurityContextSource ldapContextSource = new DefaultSpringSecurityContextSource(settings.getUrl());
            ldapContextSource.setUserDn(settings.getUser());
            ldapContextSource.setPassword(settings.getPassword());
            try {
                ldapContextSource.afterPropertiesSet();
            } catch (Exception e) {
                throw new CannotInitializeLDAPException(e);
            }
            // User search
            FilterBasedLdapUserSearch userSearch = new FilterBasedLdapUserSearch(
                    settings.getSearchBase(),
                    settings.getSearchFilter(),
                    ldapContextSource);
            userSearch.setSearchSubtree(true);
            // Bind authenticator
            BindAuthenticator bindAuthenticator = new BindAuthenticator(ldapContextSource);
            bindAuthenticator.setUserSearch(userSearch);
            // Provider
            LdapAuthenticationProvider ldapAuthenticationProvider = new LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator);
            ldapAuthenticationProvider.setUserDetailsContextMapper(new ConfigurableUserDetailsContextMapper(settings));
            // OK
            return ldapAuthenticationProvider;
        }
        // LDAP not enabled
        else {
            return null;
        }
    }

}
