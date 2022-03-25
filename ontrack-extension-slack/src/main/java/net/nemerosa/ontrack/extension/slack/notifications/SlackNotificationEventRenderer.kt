package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.extension.notifications.rendering.AbstractUrlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.ui.controller.ProjectEntityPageBuilder
import org.springframework.stereotype.Component

@Component
class SlackNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) :
    AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override fun render(projectEntity: ProjectEntity, event: Event): String {
        val pageUrl = getUrl(ProjectEntityPageBuilder.getEntityPageRelativeURI(projectEntity))
        return "[${getProjectEntityName(projectEntity)}](${pageUrl})"
    }

    override fun render(valueKey: String, value: NameValue, event: Event): String = "_${value.value}_"

}