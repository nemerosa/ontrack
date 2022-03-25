package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue
import org.springframework.stereotype.Component

@Component
class SlackNotificationEventRenderer: EventRenderer {

    override fun render(projectEntity: ProjectEntity, event: Event): String {
        TODO("Not yet implemented")
    }

    override fun render(valueKey: String, value: NameValue, event: Event): String {
        TODO("Not yet implemented")
    }
}