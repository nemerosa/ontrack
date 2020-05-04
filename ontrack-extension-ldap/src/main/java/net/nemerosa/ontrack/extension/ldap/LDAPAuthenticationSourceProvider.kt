package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component

@Component
class LDAPAuthenticationSourceProvider(
        private val cachedSettingsService: CachedSettingsService
) : AuthenticationSourceProvider {

    override val sources get() = listOf(SOURCE.enabled(cachedSettingsService.getCachedSettings(LDAPSettings::class.java).isEnabled))

    override val id: String = ID

    companion object {
        const val ID = "ldap"
        val SOURCE = AuthenticationSource(
                provider = ID,
                key = "",
                name = "LDAP authentication",
                isAllowingPasswordChange = false,
                isGroupMappingSupported = true
        )
    }
}
