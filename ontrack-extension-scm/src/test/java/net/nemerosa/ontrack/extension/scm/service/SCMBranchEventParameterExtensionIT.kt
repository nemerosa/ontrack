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

                        // Gets all the variables for this event (with the case variants)
                        val caseParameters = eventVariableService.getTemplateParameters(event, caseVariants = true)

                        // Checks all parameters
                        assertEquals(
                            mapOf(
                                "project" to project.name.lowercase(),
                                "Project" to project.name,
                                "PROJECT" to project.name.uppercase(),
                                "branch" to branch.name.lowercase(),
                                "Branch" to branch.name,
                                "BRANCH" to branch.name.uppercase(),
                                "build" to build.name.lowercase(),
                                "Build" to build.name,
                                "BUILD" to build.name.uppercase(),
                                "promotion" to pl.name.lowercase(),
                                "Promotion" to pl.name,
                                "PROMOTION" to pl.name.uppercase(),
                                "scmbranch" to "release/1.23",
                                "ScmBranch" to "release/1.23",
                                "SCMBRANCH" to "RELEASE/1.23",
                            ).toSortedMap(),
                            caseParameters.toSortedMap()
                        )
                    }
                }
            }
        }
    }

}