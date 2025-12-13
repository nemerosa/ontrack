package net.nemerosa.ontrack.extension.stash.settings

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.AbstractSettingsManager
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.SettingsRepository
import net.nemerosa.ontrack.model.support.setBoolean
import net.nemerosa.ontrack.model.support.setLong
import org.springframework.stereotype.Component

@Component
class BitbucketServerSettingsManager(
    cachedSettingsService: CachedSettingsService,
    securityService: SecurityService,
    private val settingsRepository: SettingsRepository,
) : AbstractSettingsManager<BitbucketServerSettings>(
    BitbucketServerSettings::class.java,
    cachedSettingsService,
    securityService,
) {

    override fun doSaveSettings(settings: BitbucketServerSettings) {
        settingsRepository.setLong<BitbucketServerSettings>(settings::autoMergeTimeout)
        settingsRepository.setLong<BitbucketServerSettings>(settings::autoMergeInterval)
        settingsRepository.setBoolean<BitbucketServerSettings>(settings::autoDeleteBranch)
    }

    override fun getId(): String = "bitbucket-server"

    override fun getTitle(): String = "Bitbucket Server"
}