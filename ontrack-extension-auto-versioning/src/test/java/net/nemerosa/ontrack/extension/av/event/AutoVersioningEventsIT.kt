package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.extension.av.AutoVersioningTestFixtures.createOrder
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
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
        val target = doCreateBranch()
        project {
            branch {
                build {
                    val order = createOrder(
                        targetBranch = target,
                        targetVersion = "1.1.0",
                    )
                    val event = autoVersioningEventsFactory.error(
                        order = order,
                        message = "Post processing error",
                        error = MockPostProcessingFailureException(
                            message = "Remote job failed",
                            link = "https://job.link",
                        )
                    )
                    val text = eventTemplatingService.renderEvent(
                        event,
                        context = emptyMap(),
                        renderer = htmlNotificationEventRenderer
                    )
                    assertEquals(
                        """
                            Auto versioning post-processing of <a href="http://localhost:8080/#/project/${target.project.id}">${target.project.name}</a>/<a href="http://localhost:8080/#/branch/${target.id}">${target.name}</a> for dependency <a href="http://localhost:8080/#/project/${project.id}">${project.name}</a> version "1.1.0" has failed.
    
                            <a href="https://job.link">Post processing error.</a>
                        """.trimIndent(),
                        text
                    )
                }
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
                    val text = eventTemplatingService.renderEvent(
                        event,
                        context = emptyMap(),
                        renderer = htmlNotificationEventRenderer
                    )
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

    @Test
    fun `Rendering the processing error event`() {
        val target = doCreateBranch()
        project {
            branch {
                build {
                    val order = createOrder(
                        targetBranch = target,
                        targetVersion = "1.1.0",
                    )
                    val event = autoVersioningEventsFactory.error(
                        order = order,
                        message = "Processing failed.",
                        error = RuntimeException("Processing failed because of this error.")
                    )
                    val text = eventTemplatingService.renderEvent(
                        event,
                        context = emptyMap(),
                        renderer = htmlNotificationEventRenderer
                    )
                    assertEquals(
                        """
                            Auto versioning of <a href="http://localhost:8080/#/project/${target.project.id}">${target.project.name}</a>/<a href="http://localhost:8080/#/branch/${target.id}">${target.name}</a> for dependency <a href="http://localhost:8080/#/project/${project.id}">${project.name}</a> version "1.1.0" has failed.
    
                            Processing failed.
                            
                            Error: Processing failed because of this error.
                        """.trimIndent(),
                        text
                    )
                }
            }
        }
    }

    @Test
    fun `Rendering the PR timeout event`() {
        val target = doCreateBranch()
        project {
            branch {
                build {
                    val order = createOrder(
                        targetBranch = target,
                        targetVersion = "1.1.0",
                    )
                    val event = autoVersioningEventsFactory.prMergeTimeoutError(
                        order = order,
                        pr = SCMPullRequest(
                            id = "42",
                            name = "PR-42",
                            link = "https://scm/pr/42",
                            merged = true,
                        )
                    )
                    val text = eventTemplatingService.renderEvent(
                        event,
                        context = emptyMap(),
                        renderer = htmlNotificationEventRenderer
                    )
                    assertEquals(
                        """
                            Auto versioning of <a href="http://localhost:8080/#/project/${target.project.id}">${target.project.name}</a>/<a href="http://localhost:8080/#/branch/${target.id}">${target.name}</a> for dependency <a href="http://localhost:8080/#/project/${project.id}">${project.name}</a> version "1.1.0" has failed.
    
                            Timeout while waiting for the PR to be ready to be merged.
                            
                            Pull request <a href="https://scm/pr/42">PR-42</a>
                        """.trimIndent(),
                        text
                    )
                }
            }
        }
    }

}