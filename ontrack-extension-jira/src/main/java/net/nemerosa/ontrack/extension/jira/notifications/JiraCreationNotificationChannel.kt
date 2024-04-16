package net.nemerosa.ontrack.extension.jira.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

@Component
class JiraCreationNotificationChannel :
    AbstractNotificationChannel<JiraCreationNotificationChannelConfig, JiraCreationNotificationChannelOuput>(
        JiraCreationNotificationChannelConfig::class
    ) {

    override fun publish(
        config: JiraCreationNotificationChannelConfig,
        event: Event,
        template: String?
    ): NotificationResult<JiraCreationNotificationChannelOuput> {
        TODO("Not yet implemented")
    }

    override fun toSearchCriteria(text: String): JsonNode {
        TODO("Not yet implemented")
    }

    override val type: String = "jira-creation"

    override val enabled: Boolean = true

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun getForm(c: JiraCreationNotificationChannelConfig?): Form {
        TODO("Not yet implemented")
    }

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun toText(config: JiraCreationNotificationChannelConfig): String {
        TODO("Not yet implemented")
    }

}