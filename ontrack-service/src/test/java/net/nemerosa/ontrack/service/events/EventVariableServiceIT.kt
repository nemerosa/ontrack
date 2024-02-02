package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventVariableService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EventVariableServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventVariableService: EventVariableService

    @Test
    fun `Context associated with a promotion run`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        val build = this
                        val run = promote(pl)

                        // Creates an event for this run
                        val event = eventFactory.newPromotionRun(run)

                        // Gets the templating context
                        val context = eventVariableService.getTemplateContext(event)

                        // Checks all parameters
                        assertEquals(
                            mapOf(
                                "project" to project,
                                "branch" to branch,
                                "build" to build,
                                "promotionLevel" to pl,
                                "promotionRun" to run,
                            ),
                            context
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Context associated with a validation run`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp()
                    build {
                        val build = this
                        val run = validate(vs)

                        // Creates an event for this run
                        val event = eventFactory.newValidationRun(run)

                        // Gets the templating context
                        val context = eventVariableService.getTemplateContext(event)

                        // Checks all parameters
                        assertEquals(
                            mapOf(
                                "project" to project,
                                "branch" to branch,
                                "build" to build,
                                "validationStamp" to vs,
                                "validationRun" to run,
                                "STATUS" to run.lastStatus.statusID.id,
                                "STATUS_NAME" to run.lastStatus.statusID.name,
                            ),
                            context
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Variables associated with a promotion run`() {
        asAdmin {
            project {
                branch {
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
                            ),
                            parameters
                        )
                    }
                }
            }
        }
    }


}