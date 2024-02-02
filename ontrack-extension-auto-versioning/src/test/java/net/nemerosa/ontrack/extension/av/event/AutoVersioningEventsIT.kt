package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures
import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

internal class AutoVersioningEventsIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var htmlNotificationEventRenderer: HtmlNotificationEventRenderer

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Autowired
    private lateinit var autoVersioningEventsFactory: AutoVersioningEventsFactory

    @Test
    fun `Rendering the post processing error event`() {
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
                val text = eventTemplatingService.renderEvent(event, renderer = htmlNotificationEventRenderer)
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

    @Test
    fun `Rendering the success event`() {
        val target = doCreateBranch()
        project {
            branch {
                build {
                    val order = createOrder(
                        targetBranch = target,
                        targetVersion = "1.1.0",
                    )
                    val event = autoVersioningEventsFactory.success(
                        order = order,
                        message = "Created, approved and merged.",
                        pr = SCMPullRequest(
                            id = "42",
                            name = "PR-42",
                            link = "https://scm/pr/42",
                            merged = true,
                        )
                    )
                    val text = eventTemplatingService.renderEvent(event, renderer = htmlNotificationEventRenderer)
                    assertEquals(
                        """
                            Auto versioning of <a href="http://localhost:8080/#/project/${target.project.id}">${target.project.name}</a>/<a href="http://localhost:8080/#/branch/${target.id}">${target.name}</a> for dependency <a href="http://localhost:8080/#/project/${project.id}">${project.name}</a> version "1.1.0" has been done.
    
                            Created, approved and merged.
                            
                            Pull request <a href="https://scm/pr/42">PR-42</a>
                        """.trimIndent(),
                        text
                    )
                }
            }
        }
    }

    // TODO Rendering the error event
    // TODO Rendering the PR timeout event

}