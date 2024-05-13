package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EventTemplatingServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Test
    fun `Simple rendering of entity names for a promotion run`() {

        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        val build = this
                        val run = promote(pl)

                        // Creates an event for this run
                        val event = eventFactory.newPromotionRun(run)

                        // Rendering for this event
                        val text = eventTemplatingService.render(
                            template = """
                                Build ${'$'}{build} on branch ${'$'}{branch}
                                of project ${'$'}{project} has been promoted to
                                ${'$'}{promotionLevel|uppercase}.
                            """.trimIndent(),
                            event = event,
                            context = emptyMap(),
                            renderer = PlainEventRenderer()
                        )

                        // Checking
                        assertEquals(
                            """
                                Build ${build.name} on branch ${branch.name}
                                of project ${project.name} has been promoted to
                                ${pl.name.uppercase()}.
                            """.trimIndent(),
                            text
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Simple rendering of entity names for a promotion run using the legacy template`() {

        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        val build = this
                        val run = promote(pl)

                        // Creates an event for this run
                        val event = eventFactory.newPromotionRun(run)

                        // Rendering for this event
                        val text = eventTemplatingService.render(
                            template = """
                                Build {Build} on branch {Branch}
                                of project {Project} has been promoted to
                                {Promotion|uppercase}.
                            """.trimIndent(),
                            event = event,
                            context = emptyMap(),
                            renderer = PlainEventRenderer()
                        )

                        // Checking
                        assertEquals(
                            """
                                Build ${build.name} on branch ${branch.name}
                                of project ${project.name} has been promoted to
                                ${pl.name.uppercase()}.
                            """.trimIndent(),
                            text
                        )
                    }
                }
            }
        }
    }

}