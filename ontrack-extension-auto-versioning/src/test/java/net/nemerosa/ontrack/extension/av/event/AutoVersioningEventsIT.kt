package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.extension.notifications.rendering.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.Event
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

internal class AutoVersioningEventsIT: AbstractDSLTestSupport() {

    @Autowired
    private lateinit var htmlNotificationEventRenderer: HtmlNotificationEventRenderer

    @Test
    fun `Rendering the post processing event`() {
        val source = project()
        project {
            branch {
                val event = Event.of(AutoVersioningEvents.AUTO_VERSIONING_POST_PROCESSING_ERROR)
                    .withBranch(this)
                    .withExtraProject(source)
                    .with("version", "1.1.0")
                    .with("message", "Post processing error")
                    .with("link", "urn:post-processing-job")
                    .build()
                val text = event.render(htmlNotificationEventRenderer)
                assertEquals(
                    """
                        Auto versioning post-processing of <a href="http://localhost:8080/#/project/${project.id}">${project.name}</a>/<a href="http://localhost:8080/#/branch/${id}">${name}</a> for dependency <a href="http://localhost:8080/#/project/${source.id}">${source.name}</a> version "1.1.0" has failed.

                        <a href="urn:post-processing-job">Post processing error</a>
                    """.trimIndent(),
                    text
                )
            }
        }
    }

}