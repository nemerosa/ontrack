package net.nemerosa.ontrack.extension.slack.notifications

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SlackNotificationEventRendererIT : AbstractDSLTestSupport() {

    @Autowired
    protected lateinit var eventFactory: EventFactory

    @Autowired
    protected lateinit var eventTemplatingService: EventTemplatingService

    @Autowired
    private lateinit var slackNotificationEventRenderer: SlackNotificationEventRenderer

    @Test
    fun `Rendering of entity links with the Slack renderer`() {
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
                            renderer = slackNotificationEventRenderer,
                        )

                        assertEquals(
                            """
                                Build 1.0.0 for <http://localhost:8080/#/branch/${branch.id}|${branch.name}> at <http://localhost:8080/#/project/${project.id}|${project.name}>
                                has been promoted to <http://localhost:8080/#/promotionLevel/${pl.id}|${pl.name}>.
                            """.trimIndent(),
                            text
                        )
                    }
                }
            }
        }
    }

}