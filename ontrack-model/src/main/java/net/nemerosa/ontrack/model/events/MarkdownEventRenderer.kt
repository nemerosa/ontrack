package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityPageBuilder
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class MarkdownEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override fun render(projectEntity: ProjectEntity): String {
        val pageUrl = getUrl(ProjectEntityPageBuilder.getEntityPageRelativeURI(projectEntity))
        return """[${getProjectEntityName(projectEntity)}]($pageUrl)"""
    }

    override fun render(valueKey: String, value: NameValue, event: Event): String = value.value

    override fun renderLink(text: String, href: String): String =
        """[$text]($href)"""

    override fun renderList(list: List<String>): String =
        list.joinToString("\n") { "* $it" }
}