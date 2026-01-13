package net.nemerosa.ontrack.model.templating

import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class EnvTemplatingRenderableTest {

    @Test
    fun `Rendering an environment variable`() {
        val env = mapOf("BUILD_NUMBER" to "23")
        val renderable = EnvTemplatingRenderable(env)

        assertFailsWith<TemplatingRenderableFieldRequiredException> {
            renderable.render(
                field = null,
                config = TemplatingSourceConfig(),
                renderer = PlainEventRenderer.INSTANCE
            )
        }

        assertFailsWith<TemplatingRenderableFieldRequiredException> {
            renderable.render(
                field = "",
                config = TemplatingSourceConfig(),
                renderer = PlainEventRenderer.INSTANCE
            )
        }

        assertFailsWith<TemplatingRenderableFieldNotFoundException> {
            renderable.render(
                field = "UNKNOWN",
                config = TemplatingSourceConfig(),
                renderer = PlainEventRenderer.INSTANCE
            )
        }

        assertEquals(
            "23",
            renderable.render(
                field = "BUILD_NUMBER",
                config = TemplatingSourceConfig(),
                renderer = PlainEventRenderer.INSTANCE
            )
        )
    }

}