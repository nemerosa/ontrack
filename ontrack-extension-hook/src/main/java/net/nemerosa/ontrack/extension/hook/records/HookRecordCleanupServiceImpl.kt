package net.nemerosa.ontrack.extension.hook.records

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.hook.settings.HookSettings
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HookRecordCleanupServiceImpl(
        private val cachedSettingsService: CachedSettingsService,
        private val hookRecordStore: HookRecordStore,
        private val securityService: SecurityService,
) : HookRecordCleanupService {

    override fun cleanup(): HookRecordCleanupResult {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        val settings = cachedSettingsService.getCachedSettings(HookSettings::class.java)
        // Non running
        val retentionDuration = settings.recordRetentionDuration
        val retentionDate = Time.now().minus(retentionDuration)
        val nonRunningCleanup = hookRecordStore.removeAllBefore(retentionDate, nonRunningOnly = true)
        // Any other state
        val cleanupDuration = settings.recordCleanupDuration
        val cleanupDate = retentionDate.minus(cleanupDuration)
        val anyStateCleanup = hookRecordStore.removeAllBefore(cleanupDate, nonRunningOnly = false)
        // Summary
        return HookRecordCleanupResult(
                nonRunning = nonRunningCleanup,
                anyState = anyStateCleanup
        )
    }

    override fun purge() {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        hookRecordStore.removeAll()
    }
}