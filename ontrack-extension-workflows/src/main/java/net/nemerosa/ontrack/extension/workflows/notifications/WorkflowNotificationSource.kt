package net.nemerosa.ontrack.extension.workflows.notifications

import net.nemerosa.ontrack.extension.notifications.model.NotificationSource
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class WorkflowNotificationSource : NotificationSource<WorkflowNotificationSourceDataType> {

    override val id: String = "workflow"
    override val displayName: String = "Sent by a workflow"
    override val dataType: KClass<WorkflowNotificationSourceDataType> =
        WorkflowNotificationSourceDataType::class

}