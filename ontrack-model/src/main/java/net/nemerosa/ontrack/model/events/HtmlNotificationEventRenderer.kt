package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Component

@Component
class HtmlNotificationEventRenderer(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractUrlNotificationEventRenderer(ontrackConfigProperties) {

    override val id: String = "html"
    override val name: String = "HTML"

    override fun renderStrong(value: String): String = "<b>$value</b>"

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

    override fun renderSpace(): String = "<br/><br/>"

    override fun renderSection(title: String, content: String): String =
        """
            <h3>$title</h3>
            <div>
                $content
            </div>
        """.trimIndent()
}