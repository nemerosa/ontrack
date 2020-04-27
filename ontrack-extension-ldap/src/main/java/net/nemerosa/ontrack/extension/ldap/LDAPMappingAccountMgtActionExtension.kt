package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.extension.api.AccountMgtActionExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.Action
import org.springframework.stereotype.Component

@Component
class LDAPMappingAccountMgtActionExtension(
        extensionFeature: LDAPExtensionFeature,
        private val cachedSettingsService: CachedSettingsService
) : AbstractExtension(extensionFeature), AccountMgtActionExtension {

    override fun getAction(): Action? {
        val isEnabled = cachedSettingsService.getCachedSettings(LDAPSettings::class.java).isEnabled
        return if (isEnabled) {
            Action.of("ldap-mapping", "LDAP Mapping", "ldap-mapping")
        } else {
            null
        }
    }

}