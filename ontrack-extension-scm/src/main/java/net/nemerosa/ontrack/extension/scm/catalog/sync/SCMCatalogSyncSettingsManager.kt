package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.form.yesNoField
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setString
import org.springframework.stereotype.Component

@Component
class SCMCatalogSyncSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<SCMCatalogSyncSettings>(
    SCMCatalogSyncSettings::class.java,
    cachedSettingsService,
    securityService
) {
    override fun doSaveSettings(settings: SCMCatalogSyncSettings) {
        settingsRepository.setBoolean<SCMCatalogSyncSettings>(settings::syncEnabled)
        settingsRepository.setBoolean<SCMCatalogSyncSettings>(settings::orphanDisablingEnabled)
        settingsRepository.setString<SCMCatalogSyncSettings>(settings::scm)
        settingsRepository.setString<SCMCatalogSyncSettings>(settings::config)
        settingsRepository.setString<SCMCatalogSyncSettings>(settings::repository)
    }

    override fun getSettingsForm(settings: SCMCatalogSyncSettings?): Form =
        Form.create()
            .with(
                YesNo.of(SCMCatalogSyncSettings::syncEnabled.name)
                    .label("Sync enabled")
                    .help("If synchronization of SCM catalog entries as Ontrack projects is enabled")
                    .value(settings?.syncEnabled ?: DEFAULT_SCM_CATALOG_SYNC_SETTINGS_ENABLED)
            )
            .yesNoField(
                SCMCatalogSyncSettings::orphanDisablingEnabled,
                settings?.orphanDisablingEnabled ?: DEFAULT_SCM_CATALOG_SYNC_SETTINGS_ORPHAN_DISABLED
            )
            .with(
                Text.of(SCMCatalogSyncSettings::scm.name)
                    .label("SCM filter")
                    .help("Filter on the SCM type (regex)")
                    .optional()
                    .value(settings?.scm ?: DEFAULT_SCM_CATALOG_SYNC_SETTINGS_SCM)
                    .visibleIf(SCMCatalogSyncSettings::syncEnabled.name)
            )
            .with(
                Text.of(SCMCatalogSyncSettings::config.name)
                    .label("Config filter")
                    .help("Filter on the SCM config (regex)")
                    .optional()
                    .value(settings?.config ?: DEFAULT_SCM_CATALOG_SYNC_SETTINGS_CONFIG)
                    .visibleIf(SCMCatalogSyncSettings::syncEnabled.name)
            )
            .with(
                Text.of(SCMCatalogSyncSettings::repository.name)
                    .label("Repository filter")
                    .help("Filter on the SCM repository (regex)")
                    .optional()
                    .value(settings?.repository ?: DEFAULT_SCM_CATALOG_SYNC_SETTINGS_REPOSITORY)
                    .visibleIf(SCMCatalogSyncSettings::syncEnabled.name)
            )

    override fun getId(): String = "scm-catalog-sync"

    override fun getTitle(): String = "SCM Catalog Synchronization"
}