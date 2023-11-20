package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.mock.mockScm
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventVariableService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SCMBranchEventParameterExtensionIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventVariableService: EventVariableService

    @Test
    fun `Variables associated with a promotion run`() {
        asAdmin {
            project {
                branch {
                    mockScm(this, scmBranch = "release/1.23")
                    val pl = promotionLevel()
                    build {
                        val build = this
                        val run = promote(pl)

                        // Creates an event for this run
                        val event = eventFactory.newPromotionRun(run)

                        // Gets all the variables for this event (neglecting the case variants)
                        val parameters = eventVariableService.getTemplateParameters(event, caseVariants = false)

                        // Checks all parameters
                        assertEquals(
                            mapOf(
                                "project" to project.name,
                                "branch" to branch.name,
                                "build" to build.name,
                                "promotion" to pl.name,
                                "scmBranch" to "release/1.23",
                            ),
                            parameters
                        )
                    }
                }
            }
        }
    }

}