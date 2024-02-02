package net.nemerosa.ontrack.it.events

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.events.EventTemplatingService
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

/**
 * Testing event renderers within the same framework.
 */
abstract class AbstractEventRendererTestSupport : AbstractDSLTestSupport() {

    protected val templateEntityLinksAndBuildRelease = """
                            Build ${'$'}{build.release} for ${'$'}{branch} at ${'$'}{project}
                            has been promoted to ${'$'}{promotion}.
                        """.trimIndent()

    @Autowired
    protected lateinit var eventFactory: EventFactory

    @Autowired
    protected lateinit var eventTemplatingService: EventTemplatingService

    protected fun testEntityLinksAndBuildRelease(
        renderer: EventRenderer,
        expectedText: String,
    ) {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        val run = promote(pl)

                        val event = eventFactory.newPromotionRun(run)

                        val text = eventTemplatingService.renderEvent(
                            event = event,
                            template = templateEntityLinksAndBuildRelease,
                            renderer = renderer,
                        )

                        assertEquals(
                            expectedText,
                            text
                        )
                    }
                }
            }
        }
    }

}