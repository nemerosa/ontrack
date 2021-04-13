package net.nemerosa.ontrack.extension.ldap.casc

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.context.settings.AbstractSubSettingsContext
import net.nemerosa.ontrack.extension.ldap.LDAPSettings
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import org.springframework.stereotype.Component

@Component
class LDAPSettingsContext(
    settingsManagerService: SettingsManagerService,
    cachedSettingsService: CachedSettingsService,
) : AbstractSubSettingsContext<LDAPSettings>(
    "ldap",
    LDAPSettings::class,
    settingsManagerService,
    cachedSettingsService,
) {

    override fun adjustNodeBeforeParsing(node: JsonNode): JsonNode = node.ifMissing(
        LDAPSettings::groupSearchBase to LDAPSettings.DEFAULT_GROUP_SEARCH_BASE,
        LDAPSettings::groupNameAttribute to LDAPSettings.DEFAULT_GROUP_NAME_ATTRIBUTE,
        LDAPSettings::groupSearchFilter to LDAPSettings.DEFAULT_GROUP_SEARCH_FILTER,
    )

}
