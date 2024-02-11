package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityPageBuilder
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class MarkdownEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override val id: String = "markdown"
    override val name: String = "Markdown"

    override fun render(projectEntity: ProjectEntity): String {
        val pageUrl = getUrl(ProjectEntityPageBuilder.getEntityPageRelativeURI(projectEntity))
        return """[${getProjectEntityName(projectEntity)}]($pageUrl)"""
    }

    override fun renderStrong(value: String): String = "**${value}**"

    override fun renderLink(text: String, href: String): String =
        """[$text]($href)"""

    override fun renderList(list: List<String>): String =
        list.joinToString("\n") { "* $it" }

    override fun renderSpace(): String = "\n\n"

    override fun renderSection(title: String, content: String): String =
        """
            |## $title
            |
            |$content
        """.trimMargin()
}