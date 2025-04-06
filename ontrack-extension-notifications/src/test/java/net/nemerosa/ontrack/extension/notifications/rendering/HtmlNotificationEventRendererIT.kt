package net.nemerosa.ontrack.extension.notifications.rendering

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class HtmlNotificationEventRendererIT : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var eventFactory: EventFactory

    @Autowired
    protected lateinit var eventTemplatingService: EventTemplatingService

    @Autowired
    private lateinit var htmlNotificationEventRenderer: HtmlNotificationEventRenderer

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
                            context = emptyMap(),
                            renderer = htmlNotificationEventRenderer,
                        )

                        assertEquals(
                            """
                                Build 1.0.0 for <a href="http://localhost:3000/branch/${branch.id}">${branch.name}</a> at <a href="http://localhost:3000/project/${project.id}">${project.name}</a>
                                has been promoted to <a href="http://localhost:3000/promotionLevel/${pl.id}">${pl.name}</a>.
                            """.trimIndent(),
                            text
                        )
                    }
                }
            }
        }
    }
}