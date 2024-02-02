package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.model.events.AbstractUrlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.structure.ProjectEntityPageBuilder
import org.springframework.stereotype.Component

@Component
class SlackNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) :
    AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override fun render(projectEntity: ProjectEntity): String {
        val pageUrl = getUrl(ProjectEntityPageBuilder.getEntityPageRelativeURI(projectEntity))
        return "<$pageUrl|${getProjectEntityName(projectEntity)}>"
    }

    override fun render(valueKey: String, value: NameValue, event: Event): String = "_${value.value}_"

    override fun renderLink(text: NameValue, link: NameValue, event: Event): String =
        """<${link.value}|${text.value}>"""

}