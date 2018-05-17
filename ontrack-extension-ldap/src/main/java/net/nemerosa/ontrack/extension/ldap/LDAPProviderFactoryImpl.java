package net.nemerosa.ontrack.extension.ldap;

import net.nemerosa.ontrack.model.security.SecurityRole;
import net.nemerosa.ontrack.model.settings.CachedSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LDAPProviderFactoryImpl implements LDAPProviderFactory {

    private final CachedSettingsService cachedSettingsService;
    private final LdapAuthoritiesPopulator authoritiesPopulator = (userData, username) -> AuthorityUtils.createAuthorityList(SecurityRole.USER.name());

    private static final String CACHE_KEY = "0";
    private final Map<String, LdapAuthenticationProvider> cache = new ConcurrentHashMap<>();

    @Autowired
    public LDAPProviderFactoryImpl(CachedSettingsService cachedSettingsService) {
        this.cachedSettingsService = cachedSettingsService;
    }

    @Override
    public void invalidate() {
        cache.clear();
    }

    public LdapAuthenticationProvider getProvider() {
        return cache.computeIfAbsent(CACHE_KEY, x -> loadProvider());
    }

    private LdapAuthenticationProvider loadProvider() {
        LDAPSettings settings = cachedSettingsService.getCachedSettings(LDAPSettings.class);
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
            ldapAuthenticationProvider.setUserDetailsContextMapper(new ConfigurableUserDetailsContextMapper(settings, ldapContextSource));
            // OK
            return ldapAuthenticationProvider;
        }
        // LDAP not enabled
        else {
            return null;
        }
    }

}
