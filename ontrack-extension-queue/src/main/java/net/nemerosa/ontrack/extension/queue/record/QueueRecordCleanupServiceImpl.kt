package net.nemerosa.ontrack.extension.queue.record

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.queue.settings.QueueSettings
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class QueueRecordCleanupServiceImpl(
        private val cachedSettingsService: CachedSettingsService,
        private val queueRecordStore: QueueRecordStore,
        private val securityService: SecurityService,
) : QueueRecordCleanupService {

    override fun cleanup(): QueueRecordCleanupResult {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        val settings = cachedSettingsService.getCachedSettings(QueueSettings::class.java)
        // Non running
        val retentionDuration = settings.recordRetentionDuration
        val retentionDate = Time.now().minus(retentionDuration)
        val nonRunningCleanup = queueRecordStore.removeAllBefore(retentionDate, nonRunningOnly = true)
        // Any other state
        val cleanupDuration = settings.recordCleanupDuration
        val cleanupDate = retentionDate.minus(cleanupDuration)
        val anyStateCleanup = queueRecordStore.removeAllBefore(cleanupDate, nonRunningOnly = false)
        // Summary
        return QueueRecordCleanupResult(
                nonRunning = nonRunningCleanup,
                anyState = anyStateCleanup
        )
    }

    override fun purge() {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        queueRecordStore.removeAll()
    }
}