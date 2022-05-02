package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.notifications.NotificationsJobs
import net.nemerosa.ontrack.job.*
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.springframework.stereotype.Component
import java.util.stream.Stream

@Component
class NotificationRecordingCleanupJob(
    private val cachedSettingsService: CachedSettingsService,
    private val notificationRecordingService: NotificationRecordingService,
) : JobOrchestratorSupplier {

    override fun collectJobRegistrations(): Stream<JobRegistration> = listOf(
        createNotificationRecordingCleanupJobRegistration()
    ).stream()

    private fun createNotificationRecordingCleanupJobRegistration() = JobRegistration(
        job = createNotificationRecordingCleanupJob(),
        schedule = Schedule.everySeconds(cachedSettingsService.getCachedSettings(NotificationRecordingSettings::class.java).cleanupIntervalSeconds),
    )

    private fun createNotificationRecordingCleanupJob() = object : Job {

        override fun getKey(): JobKey =
            NotificationsJobs.category
                .getType("recording-cleanup").withName("Notification recordings cleanup")
                .getKey("main")

        override fun getTask() = JobRun {
            val settings = cachedSettingsService.getCachedSettings(NotificationRecordingSettings::class.java)
            notificationRecordingService.clear(settings.retentionSeconds)
        }

        override fun getDescription(): String =
            "Cleanup of notification recordings"

        override fun isDisabled(): Boolean = false
    }
}