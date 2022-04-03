package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.stereotype.Component

@Component
class NotificationRecordingMutations(
    private val notificationRecordingService: NotificationRecordingService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        unitMutation<DeleteNotificationRecordsInput>(
            name = "deleteNotificationRecords",
            description = "Deleting notification records"
        ) { input ->
            if (input.retentionSeconds != null && input.retentionSeconds > 0) {
                notificationRecordingService.clear(input.retentionSeconds)
            } else {
                notificationRecordingService.clearAll()
            }
        }
    )
}

data class DeleteNotificationRecordsInput(
    @APIDescription("Retention period, in seconds. 0 or null for all records.")
    val retentionSeconds: Long? = 0,
)
