package net.nemerosa.ontrack.extension.notifications.rendering

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PlainNotificationEventRendererIT : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var eventFactory: EventFactory

    @Autowired
    protected lateinit var eventTemplatingService: EventTemplatingService

    private val plainEventRenderer = PlainEventRenderer.INSTANCE

    @Test
    fun `Rendering of entity links with the HTML renderer`() {
        asAdmin {
            project {
                branch {
                    val pl = promotionLevel()
                    build {
                        setProperty(this, ReleasePropertyType::class.java, ReleaseProperty("1.0.0"))
                        val run = promote(pl)

                        val event = eventFactory.newPromotionRun(run)

                        val text = eventTemplatingService.renderEvent(
                            event = event,
                            template = """
                                Build ${'$'}{build.release} for ${'$'}{branch} at ${'$'}{project}
                                has been promoted to ${'$'}{promotionLevel}.
                            """.trimIndent(),
                            renderer = plainEventRenderer,
                        )

                        assertEquals(
                            """
                                Build 1.0.0 for ${branch.name} at ${project.name}
                                has been promoted to ${pl.name}.
                            """.trimIndent(),
                            text
                        )
                    }
                }
            }
        }
    }
}