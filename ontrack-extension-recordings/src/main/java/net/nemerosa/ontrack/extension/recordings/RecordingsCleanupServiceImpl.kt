package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.recordings.store.RecordingsStore
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RecordingsCleanupServiceImpl(
        private val securityService: SecurityService,
        private val recordingsConfigProperties: RecordingsConfigProperties,
        private val recordingsStore: RecordingsStore,
) : RecordingsCleanupService {

    override fun <R : Recording> cleanup(extension: RecordingsExtension<R>) {
        securityService.checkGlobalFunction(ApplicationManagement::class.java)
        // Configuration
        val config = recordingsConfigProperties.cleanup[extension.id]
                ?: RecordingsConfigProperties.CleanupProperties()
        // Non running
        val retentionDuration = config.retention
        val retentionDate = Time.now().minus(retentionDuration)
        recordingsStore.removeAllBefore(extension.id, retentionDate, nonRunningOnly = true)
        // Any other state
        val cleanupDuration = config.cleanup
        val cleanupDate = retentionDate.minus(cleanupDuration)
        recordingsStore.removeAllBefore(extension.id, cleanupDate, nonRunningOnly = false)
    }
}