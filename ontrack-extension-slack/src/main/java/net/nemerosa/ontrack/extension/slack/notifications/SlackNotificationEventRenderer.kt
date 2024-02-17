package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.model.events.AbstractUrlNotificationEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class SlackNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override val id: String = "slack"
    override val name: String = "Slack"

    override fun renderStrong(value: String): String = "*${value}*"

    override fun renderLink(text: String, href: String): String = """<$href|$text>"""

    override fun renderList(list: List<String>): String =
        list.joinToString("\n") { "* $it" }

    override fun renderSpace(): String = "\n\n"

    override fun renderSection(title: String, content: String): String =
        """
            |*$title*
            |
            |$content
        """.trimMargin()
}