package net.nemerosa.ontrack.extension.notifications.trigger

import net.nemerosa.ontrack.model.trigger.Trigger
import org.springframework.stereotype.Component

@Component
class NotificationRecordTrigger : Trigger<NotificationRecordTriggerData> {

    override val id: String = "notification-record"
    override val displayName: String = "Notification"

    override fun filterCriteria(token: String, criterias: MutableList<String>, params: MutableMap<String, Any?>) {
        criterias += "TRIGGER_DATA::JSONB->>'recordId' = :recordId"
        params["recordId"] = token
    }
}