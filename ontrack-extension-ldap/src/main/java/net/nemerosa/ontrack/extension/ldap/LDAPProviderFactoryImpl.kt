package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.SecurityRole
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.ldap.DefaultSpringSecurityContextSource
import org.springframework.security.ldap.authentication.BindAuthenticator
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

/**
 * Creates, configures and caches a fully configured [LdapAuthenticationProvider] based
 * on the [LDAP settings][LDAPSettings].
 */
@Service
class LDAPProviderFactoryImpl(
        private val cachedSettingsService: CachedSettingsService,
        private val applicationLogService: ApplicationLogService
) : LDAPProviderFactory {

    private val authoritiesPopulator = LdapAuthoritiesPopulator { _: DirContextOperations?, _: String? -> AuthorityUtils.createAuthorityList(SecurityRole.USER.name) }

    private val cache: MutableMap<String, CachedLdap> = ConcurrentHashMap()

    /**
     * Clears the internal cache.
     */
    override fun invalidate() {
        cache.clear()
    }

    /**
     * Checks the cache
     */
    override val provider: LdapAuthenticationProvider?
        get() = cache.computeIfAbsent(CACHE_KEY) { loadProvider() }.provider

    private fun loadProvider(): CachedLdap {
        val settings = cachedSettingsService.getCachedSettings(LDAPSettings::class.java)
        return if (settings.isEnabled) {
            try {
                // LDAP context
                val ldapContextSource = DefaultSpringSecurityContextSource(settings.url)
                ldapContextSource.userDn = settings.user
                ldapContextSource.password = settings.password
                try {
                    ldapContextSource.afterPropertiesSet()
                } catch (e: Exception) {
                    throw CannotInitializeLDAPException(e)
                }
                // User search
                val userSearch = FilterBasedLdapUserSearch(
                        settings.searchBase,
                        settings.searchFilter,
                        ldapContextSource)
                userSearch.setSearchSubtree(true)
                // Bind authenticator
                val bindAuthenticator = BindAuthenticator(ldapContextSource)
                bindAuthenticator.setUserSearch(userSearch)
                // Provider
                val ldapAuthenticationProvider = LdapAuthenticationProvider(bindAuthenticator, authoritiesPopulator)
                ldapAuthenticationProvider.setUserDetailsContextMapper(ConfigurableUserDetailsContextMapper(settings, ldapContextSource))
                // OK
                CachedLdap(ldapAuthenticationProvider)
            } catch (ex: Exception) {
                applicationLogService.log(
                        ApplicationLogEntry.error(
                                ex,
                                NameDescription.nd("ldap-config", "LDAP Configuration"),
                                "Cannot configure the LDAP connection"
                        )
                )
                CachedLdap(null)
            }
        } else {
            CachedLdap(null)
        }
    }

    companion object {
        /**
         * Internal cache key for the LDAP
         */
        private const val CACHE_KEY = "0"
    }

    /**
     * Cache entry (to distinguish between "not initialized" and "not configured")
     */
    private class CachedLdap(val provider: LdapAuthenticationProvider?)

}