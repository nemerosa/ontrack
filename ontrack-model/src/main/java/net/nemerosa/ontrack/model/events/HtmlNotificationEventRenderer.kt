package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.events.AbstractUrlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.structure.ProjectEntityPageBuilder
import org.springframework.stereotype.Component

@Component
class HtmlNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override fun render(projectEntity: ProjectEntity): String {
        val pageUrl = getUrl(ProjectEntityPageBuilder.getEntityPageRelativeURI(projectEntity))
        return """<a href="$pageUrl">${getProjectEntityName(projectEntity)}</a>"""
    }

    override fun render(valueKey: String, value: NameValue, event: Event): String = value.value

    override fun renderLink(text: String, href: String): String {
        return """<a href="$href">$text</a>"""
    }

    override fun renderList(list: List<String>): String {
        return """
            <ul>
                ${list.joinToString("\n") { """    <li>$it</li>""" }}
            </ul>
        """.trimIndent()
    }
}