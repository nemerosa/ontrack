package net.nemerosa.ontrack.extension.jira.notifications

import net.nemerosa.ontrack.model.events.AbstractUrlNotificationEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class JiraNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override val id: String = "jira"
    override val name: String = "Jira"

    override fun renderStrong(value: String): String = "*$value*"

    override fun renderLink(text: String, href: String): String = "[$text|$href]"

    override fun renderList(list: List<String>): String =
        list.joinToString("\n") { "* $it" }

    override fun renderSpace(): String = "\n\n"

    override fun renderSection(title: String, content: String): String =
        """
            |h3. $title
            |
            |$content
        """.trimMargin()
}
