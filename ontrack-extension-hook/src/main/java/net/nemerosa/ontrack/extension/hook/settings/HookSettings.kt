package net.nemerosa.ontrack.extension.hook.settings

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import net.nemerosa.ontrack.json.SimpleDurationDeserializer
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import java.time.Duration

@APIDescription("Hook settings")
data class HookSettings(
        @APIDescription("Maximum number of days to keep hook records for non-running requests")
        @APILabel("Records retention")
        @JsonDeserialize(using = SimpleDurationDeserializer::class)
        val recordRetentionDuration: Duration = DEFAULT_RECORD_RETENTION_DURATION,
        @APIDescription("Maximum number of days to keep queue records for all kinds of hook requests (counted _after_ the retention)")
        @APILabel("Records cleanup")
        @JsonDeserialize(using = SimpleDurationDeserializer::class)
        val recordCleanupDuration: Duration = DEFAULT_RECORD_CLEANUP_DURATION,
) {
    companion object {

        /**
         * Default value for [HookSettings.recordRetentionDuration]
         */
        val DEFAULT_RECORD_RETENTION_DURATION: Duration = Duration.ofDays(14)

        /**
         * Default value for [HookSettings.recordCleanupDuration]
         */
        val DEFAULT_RECORD_CLEANUP_DURATION: Duration = Duration.ofDays(90)

    }
}
