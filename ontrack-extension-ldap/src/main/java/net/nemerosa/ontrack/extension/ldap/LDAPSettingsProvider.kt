package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.settings.SettingsProvider
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.getPassword
import net.nemerosa.ontrack.model.support.getString
import org.springframework.stereotype.Component

@Component
class LDAPSettingsProvider(
        private val settingsRepository: SettingsRepository,
        private val encryptionService: EncryptionService
) : SettingsProvider<LDAPSettings> {

    override fun getSettings(): LDAPSettings = LDAPSettings(
            settingsRepository.getBoolean(LDAPSettings::class.java, "enabled", false),
            settingsRepository.getString(LDAPSettings::url, ""),
            settingsRepository.getString(LDAPSettings::searchBase, ""),
            settingsRepository.getString(LDAPSettings::searchFilter, ""),
            settingsRepository.getString(LDAPSettings::user, ""),
            settingsRepository.getPassword(LDAPSettings::password, "", encryptionService::decrypt),
            settingsRepository.getString(LDAPSettings::fullNameAttribute, ""),
            settingsRepository.getString(LDAPSettings::emailAttribute, ""),
            settingsRepository.getString(LDAPSettings::groupAttribute, ""),
            settingsRepository.getString(LDAPSettings::groupFilter, ""),
            settingsRepository.getString(LDAPSettings::groupNameAttribute, "cn"),
            settingsRepository.getString(LDAPSettings::groupSearchBase, ""),
            settingsRepository.getString(LDAPSettings::groupSearchFilter, "(member={0})")
    )

    override fun getSettingsClass(): Class<LDAPSettings> = LDAPSettings::class.java

}
