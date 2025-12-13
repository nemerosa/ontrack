package net.nemerosa.ontrack.extension.scm.catalog.sync

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

    override fun getId(): String = "scm-catalog-sync"

    override fun getTitle(): String = "SCM Catalog Synchronization"
}