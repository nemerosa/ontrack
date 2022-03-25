package net.nemerosa.ontrack.extension.notifications.rendering

import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.service.events.EventFactoryImpl
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class HtmlNotificationEventRendererTest {

    private val eventFactory: EventFactory = EventFactoryImpl()
    private val htmlNotificationEventRenderer = HtmlNotificationEventRenderer(
        OntrackConfigProperties().apply {
            url = "https://ontrack.nemerosa.net"
        }
    )

    @Test
    fun `New project`() {
        val project = project()
        val event = eventFactory.newProject(project)
        val text = event.render(htmlNotificationEventRenderer)
        assertEquals(
            """New project <a href="https://ontrack.nemerosa.net/#/project/${project.id}">${project.name}</a>.""",
            text
        )
    }

    private fun project() = Project.of(NameDescription.nd("PRJ", "")).withId(ID.of(1))

}