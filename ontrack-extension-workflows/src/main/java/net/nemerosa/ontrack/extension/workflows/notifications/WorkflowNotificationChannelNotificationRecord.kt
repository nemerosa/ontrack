package net.nemerosa.ontrack.extension.workflows.notifications

data class WorkflowNotificationChannelNotificationRecord(
    val recordId: String,
) {

    companion object {
        const val CONTEXT_NOTIFICATION_RECORD_ID = "notificationRecordId"
    }

}