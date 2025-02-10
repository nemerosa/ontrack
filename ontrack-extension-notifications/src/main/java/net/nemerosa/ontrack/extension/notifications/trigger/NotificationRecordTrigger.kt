package net.nemerosa.ontrack.extension.notifications.trigger

import net.nemerosa.ontrack.model.trigger.Trigger
import org.springframework.stereotype.Component

@Component
class NotificationRecordTrigger : Trigger<NotificationRecordTriggerData> {

    override val id: String = "notification-record"

}