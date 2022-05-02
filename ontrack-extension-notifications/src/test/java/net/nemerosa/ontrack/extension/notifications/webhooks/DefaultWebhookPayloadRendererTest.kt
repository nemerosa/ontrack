package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.model.events.EventFactoryImpl
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import org.junit.jupiter.api.Test

internal class DefaultWebhookPayloadRendererTest {

    @Test
    fun `Rendering a new project event`() {
        val renderer = DefaultWebhookPayloadRenderer()
        val project = Project.of(NameDescription.nd("prj", "New project")).withId(ID.of(1))
        val event = EventFactoryImpl().newProject(project)
        val payload = WebhookPayload(type = "event", data = event)
        renderer.render(payload)
    }

}