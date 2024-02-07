package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.model.events.AbstractUrlNotificationEventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityPageBuilder
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class SlackNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override val id: String = "slack"
    override val name: String = "Slack"

    override fun render(projectEntity: ProjectEntity): String {
        val pageUrl = getUrl(ProjectEntityPageBuilder.getEntityPageRelativeURI(projectEntity))
        return "<$pageUrl|${getProjectEntityName(projectEntity)}>"
    }

    override fun renderStrong(value: String): String = "**${value}**"

    override fun renderLink(text: String, href: String): String = """<$href|$text>"""

    override fun renderList(list: List<String>): String =
        list.joinToString("\n") { "* $it" }

}