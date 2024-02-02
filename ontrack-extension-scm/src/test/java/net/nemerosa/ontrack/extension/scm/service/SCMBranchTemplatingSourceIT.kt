package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.mock.mockScm
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SCMBranchTemplatingSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Test
    fun `Rendering the SCM branch`() {
        asAdmin {
            project {
                branch {
                    mockScm(this, scmBranch = "release/1.23")
                    val pl = promotionLevel()
                    build {
                        val run = promote(pl)

                        // Creates an event for this run
                        val event = eventFactory.newPromotionRun(run)

                        // Rendering
                        val text = eventTemplatingService.render(
                            template = "Branch ${'$'}{branch.scmBranch|urlencode} has been promoted to ${'$'}{promotionLevel}",
                            event = event,
                            renderer = PlainEventRenderer.INSTANCE,
                        )

                        // Check
                        assertEquals(
                            """Branch release%2F1.23 has been promoted to ${pl.name}""",
                            text
                        )
                    }
                }
            }
        }
    }

}