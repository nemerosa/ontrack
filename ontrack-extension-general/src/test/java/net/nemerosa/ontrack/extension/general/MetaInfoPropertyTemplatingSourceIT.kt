package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class MetaInfoPropertyTemplatingSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Test
    fun `Build with meta information link`() {
        asAdmin {
            project {
                branch {
                    build {
                        val build = this
                        metaInfoProperty(
                            build, MetaInfoPropertyItem(
                                name = "meta-key",
                                category = "my-cat",
                                value = "some-value",
                                link = "uri://some-link"
                            )
                        )

                        val event = eventFactory.newBuild(build)

                        assertEquals(
                            """Link to ticket: <a href="uri://some-link">some-value</a>""",
                            eventTemplatingService.render(
                                template = "Link to ticket: ${'$'}{build.meta?name=meta-key&category=my-cat&link=true}",
                                event = event,
                                renderer = HtmlNotificationEventRenderer(OntrackConfigProperties())
                            )
                        )
                    }
                }
            }
        }
    }

}