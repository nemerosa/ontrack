package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Form.Companion.create
import net.nemerosa.ontrack.model.form.Password
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setString
import org.springframework.stereotype.Component

@Component
class LDAPSettingsManager(
        cachedSettingsService: CachedSettingsService,
        securityService: SecurityService,
        private val settingsRepository: SettingsRepository,
        private val encryptionService: EncryptionService,
        private val ldapProviderFactory: LDAPProviderFactory
) : AbstractSettingsManager<LDAPSettings>(
        LDAPSettings::class.java,
        cachedSettingsService,
        securityService
) {

    override fun doSaveSettings(settings: LDAPSettings) {
        ldapProviderFactory.invalidate()
        settingsRepository.setBoolean(LDAPSettings::class.java, "enabled", settings.isEnabled)
        if (settings.isEnabled) {
            settingsRepository.setString<LDAPSettings>(settings::url)
            settingsRepository.setString<LDAPSettings>(settings::searchBase)
            settingsRepository.setString<LDAPSettings>(settings::searchFilter)
            settingsRepository.setString<LDAPSettings>(settings::user)
            settingsRepository.setPassword(LDAPSettings::class.java, LDAPSettings::password.name, settings.password, true) { plain: String? -> encryptionService.encrypt(plain) }
            settingsRepository.setString<LDAPSettings>(settings::fullNameAttribute)
            settingsRepository.setString<LDAPSettings>(settings::emailAttribute)
            settingsRepository.setString<LDAPSettings>(settings::groupAttribute)
            settingsRepository.setString<LDAPSettings>(settings::groupFilter)
            settingsRepository.setString<LDAPSettings>(settings::groupNameAttribute)
            settingsRepository.setString<LDAPSettings>(settings::groupSearchBase)
            settingsRepository.setString<LDAPSettings>(settings::groupSearchFilter)
        }
    }

    override fun getSettingsForm(settings: LDAPSettings): Form {
        return create()
                .with(
                        YesNo.of("enabled")
                                .label("Enable LDAP authentication")
                                .value(settings.isEnabled)
                )
                .with(
                        Text.of("url")
                                .visibleIf("enabled")
                                .label("URL")
                                .help("URL to the LDAP server. For example: https://ldap.nemerosa.com:636")
                                .value(settings.url)
                )
                .with(
                        Text.of("user")
                                .visibleIf("enabled")
                                .label("User")
                                .help("Name of the user used to connect to the LDAP server.")
                                .optional()
                                .value(settings.user)
                )
                .with(
                        Password.of("password")
                                .visibleIf("enabled")
                                .label("Password")
                                .help("Password of the user used to connect to the LDAP server.")
                                .optional()
                                .value("") // Password never sent to the client
                )
                .with(
                        Text.of("searchBase")
                                .visibleIf("enabled")
                                .label("Search base")
                                .help("Query to get the user. For example: dc=nemerosa,dc=com")
                                .optional()
                                .value(settings.searchBase)
                )
                .with(
                        Text.of("searchFilter")
                                .visibleIf("enabled")
                                .label("Search filter")
                                .help("Filter on the user query. {0} will be replaced by the user name. For example: (sAMAccountName={0})")
                                .optional()
                                .value(settings.searchFilter)
                )
                .with(
                        Text.of("fullNameAttribute")
                                .visibleIf("enabled")
                                .label("Full name attribute")
                                .help("Name of the attribute that contains the full name of the user. Defaults to cn")
                                .optional()
                                .value(settings.fullNameAttribute)
                )
                .with(
                        Text.of("emailAttribute")
                                .visibleIf("enabled")
                                .label("Email attribute")
                                .help("Name of the attribute that contains the email of the user. Defaults to email")
                                .optional()
                                .value(settings.emailAttribute)
                )
                .with(
                        Text.of("groupAttribute")
                                .visibleIf("enabled")
                                .label("Group attribute")
                                .help("Name of the attribute that contains the groups the user belongs to. Defaults to memberOf)")
                                .optional()
                                .value(settings.groupAttribute)
                )
                .with(
                        Text.of("groupFilter")
                                .visibleIf("enabled")
                                .label("Group filter")
                                .help("Name of the OU field used to filter groups a user belongs to (optional).")
                                .optional()
                                .value(settings.groupFilter)
                )
                .with(
                        Text.of("groupNameAttribute")
                                .visibleIf("enabled")
                                .label("Group name attribute")
                                .help("The ID of the attribute which contains the name for a group (optional, defaults to cn)")
                                .optional()
                                .value(settings.groupNameAttribute)
                )
                .with(
                        Text.of("groupSearchBase")
                                .visibleIf("enabled")
                                .label("Group search base")
                                .help("The base DN from which the search for group membership should be performed (optional)")
                                .optional()
                                .value(settings.groupSearchBase)
                )
                .with(
                        Text.of("groupSearchFilter")
                                .visibleIf("enabled")
                                .label("Group search filter")
                                .help("The pattern to be used for the user search. {0} is the user's DN (optional, default to (member={0}))")
                                .optional()
                                .value(settings.groupSearchFilter)
                )
    }

    override fun getId(): String = "ldap"

    override fun getTitle(): String = "LDAP settings"

}

