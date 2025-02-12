package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.common.safeHtml
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class HtmlNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override val id: String = "html"
    override val name: String = "HTML"


    override fun renderStrong(value: String): String = "<b>${value.safeHtml}</b>"

    override fun renderLink(text: String, href: String): String {
        return """<a href="$href">${text.safeHtml}</a>"""
    }

    override fun renderList(list: List<String>): String {
        return """
            <ul>
                ${list.joinToString("\n") { """    <li>${it.safeHtml}</li>""" }}
            </ul>
        """.trimIndent()


    }

    override fun renderSpace(): String = "<br/><br/>"

    override fun renderSection(title: String, content: String): String =
        """
            <h3>${title.safeHtml}</h3>
            <div>
                ${content.safeHtml}
            </div>
        """.trimIndent()
}