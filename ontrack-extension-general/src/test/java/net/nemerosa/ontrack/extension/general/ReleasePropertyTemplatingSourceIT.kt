package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class ReleasePropertyTemplatingSourceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var eventTemplatingService: EventTemplatingService

    @Test
    fun `Build with release`() {
        asAdmin {
            project {
                branch {
                    build {
                        val build = this
                        releaseProperty(build, "1.0.0")

                        val event = eventFactory.newBuild(build)

                        assertEquals(
                            "Release: 1.0.0",
                            eventTemplatingService.render(
                                template = "Release: ${'$'}{build.release}",
                                event = event,
                                renderer = PlainEventRenderer.INSTANCE
                            )
                        )
                    }
                }
            }
        }
    }

    @Test
    fun `Build without release`() {
        asAdmin {
            project {
                branch {
                    build {
                        val build = this

                        val event = eventFactory.newBuild(build)

                        assertEquals(
                            "Release: ",
                            eventTemplatingService.render(
                                template = "Release: ${'$'}{build.release}",
                                event = event,
                                renderer = PlainEventRenderer.INSTANCE
                            )
                        )
                    }
                }
            }
        }
    }

}