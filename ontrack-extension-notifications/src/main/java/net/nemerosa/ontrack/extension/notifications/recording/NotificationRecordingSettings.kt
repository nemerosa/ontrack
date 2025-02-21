package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import java.time.Duration

data class NotificationRecordingSettings(
    @APIDescription("Is the recording of notifications enabled?")
    @APILabel("Enabled")
    val enabled: Boolean = DEFAULT_ENABLED,
    @APIDescription("Number of seconds to keep the recordings")
    @APILabel("Retention time")
    val retentionSeconds: Long = DEFAULT_RETENTION_SECONDS,
    @APIDescription("Interval between each cleanup of the recordings")
    @APILabel("Cleanup interval")
    val cleanupIntervalSeconds: Long = DEFAULT_CLEANUP_INTERVAL_SECONDS,
) {
    companion object {
        const val DEFAULT_ENABLED = true
        val DEFAULT_RETENTION_SECONDS: Long = Duration.ofDays(14).toSeconds()
        val DEFAULT_CLEANUP_INTERVAL_SECONDS: Long = Duration.ofHours(24).toSeconds()
    }
}