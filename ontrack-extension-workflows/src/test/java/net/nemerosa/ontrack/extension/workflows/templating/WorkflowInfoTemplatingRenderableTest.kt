package net.nemerosa.ontrack.extension.workflows.templating

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceFixtures
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingRenderableFieldNotFoundException
import net.nemerosa.ontrack.model.templating.TemplatingRenderableFieldRequiredException
import org.junit.jupiter.api.Test
import kotlin.math.exp
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class WorkflowInfoTemplatingRenderableTest {

    @Test
    fun `Field is required`() {
        val renderable = WorkflowInfoTemplatingRenderable(
            workflowInstance = WorkflowInstanceFixtures.simpleLinear()
        )
        assertFailsWith<TemplatingRenderableFieldRequiredException> {
            renderable.render(
                field = null,
                configMap = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE
            )
        }
    }

    @Test
    fun `Unknown field`() {
        val renderable = WorkflowInfoTemplatingRenderable(
            workflowInstance = WorkflowInstanceFixtures.simpleLinear()
        )
        assertFailsWith<TemplatingRenderableFieldNotFoundException> {
            renderable.render(
                field = "xxxx",
                configMap = emptyMap(),
                renderer = PlainEventRenderer.INSTANCE
            )
        }
    }

    @Test
    fun `Start time when not started in blank`() {
        val ref = Time.now
        val renderable = WorkflowInfoTemplatingRenderable(
            workflowInstance = WorkflowInstanceFixtures.simpleLinear(timestamp = ref)
        )
        val text = renderable.render(
            field = "start",
            configMap = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE
        )
        assertEquals("", text)
    }

    @Test
    fun `Start time when not started is rendered as ISO`() {
        val ref = Time.now
        val expected = Time.store(ref)
        val instance = WorkflowInstanceFixtures.simpleLinear(timestamp = ref).run {
            startNode("start", ref)
        }
        val renderable = WorkflowInfoTemplatingRenderable(
            workflowInstance = instance
        )
        val text = renderable.render(
            field = "start",
            configMap = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE
        )
        assertEquals(expected, text)
    }

}