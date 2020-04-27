package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

@Component
class LDAPAuthenticationSourceProvider(
        private val cachedSettingsService: CachedSettingsService
) : AuthenticationSourceProvider {

    override val source: AuthenticationSource = SOURCE

    override val isEnabled: Boolean
        get() = cachedSettingsService.getCachedSettings(LDAPSettings::class.java).isEnabled

    companion object {
        val SOURCE = AuthenticationSource(
                id = "ldap",
                name = "LDAP authentication",
                isAllowingPasswordChange = false,
                isGroupMappingSupported = true
        )
    }
}
