package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.mock.MockSCMTester
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

// TODO Testing with HTML renderer
// TODO Testing with Markdown renderer
// TODO Testing with dependency change log
// TODO useProject config parameter to get change logs across branches

class PromotionRunChangeLogTemplatingSourceIT: AbstractDSLTestSupport() {

    @Autowired
    private lateinit var mockSCMTester: MockSCMTester

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Test
    fun `Getting a change log in a template`() {
        asAdmin {
            mockSCMTester.withMockSCMRepository {
                project {
                    branch {
                        configureMockSCMBranch()

                        val pl = promotionLevel()

                        build {}
                        val from = build {
                            // Mock termination commit
                            repositoryIssue("ISS-20", "Last issue before the change log")
                            withRepositoryCommit("ISS-20 Last commit before the change log")
                            // Promotion boundary
                            promote(pl)
                        }
                        build {
                            repositoryIssue("ISS-21", "Some new feature")
                            withRepositoryCommit("ISS-21 Some commits for a feature", property = false)
                            withRepositoryCommit("ISS-21 Some fixes for a feature")
                        }
                        build {
                            repositoryIssue("ISS-22", "Some fixes are needed")
                            withRepositoryCommit("ISS-22 Fixing some bugs")
                        }
                        build {
                            repositoryIssue("ISS-23", "Some nicer UI")
                            withRepositoryCommit("ISS-23 Fixing some CSS")

                            // Promotion boundary
                            val run = promote(pl)
                            val event = eventFactory.newPromotionRun(run)

                            // Template to render
                            val template = """
                                Version ${'$'}{project} ${'$'}{build} has been released.
                                
                                ${'$'}{promotionRun.changelog}
                            """.trimIndent()

                            // Rendering
                            val text = eventTemplatingService.render(
                                template = template,
                                event = event,
                                renderer = PlainEventRenderer.INSTANCE
                            )

                            // OK
                            assertEquals(
                                """
                                    Version ${project.name} $name has been released.
                                    
                                    * ISS-21 Some new feature
                                    * ISS-22 Some fixes are needed
                                    * ISS-23 Some nicer UI
                                """.trimIndent(),
                                text
                            )
                        }
                    }
                }
            }
        }
    }

}