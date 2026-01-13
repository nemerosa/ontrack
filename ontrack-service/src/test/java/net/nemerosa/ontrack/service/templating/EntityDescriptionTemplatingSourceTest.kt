package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.PromotionRunFixtures
import net.nemerosa.ontrack.model.templating.TemplatingSource
import net.nemerosa.ontrack.model.templating.TemplatingSourceConfig
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EntityDescriptionTemplatingSourceTest {

    private val source: TemplatingSource = EntityDescriptionTemplatingSource()

    @Test
    fun `Promotion run description`() {
        val run = PromotionRunFixtures.testPromotionRun(description = "My description")
        val text = source.render(run, TemplatingSourceConfig(), PlainEventRenderer.INSTANCE)
        assertEquals("My description", text)
    }

    @Test
    fun `Promotion run without description`() {
        val run = PromotionRunFixtures.testPromotionRun()
        val text = source.render(run, TemplatingSourceConfig(), PlainEventRenderer.INSTANCE)
        assertEquals("", text)
    }

    @Test
    fun `Promotion run without description and a default`() {
        val run = PromotionRunFixtures.testPromotionRun()
        val text = source.render(
            entity = run,
            config = TemplatingSourceConfig.fromMap("default" to "Other description"),
            renderer = PlainEventRenderer.INSTANCE
        )
        assertEquals("Other description", text)
    }

    @Test
    fun `Promotion run with description and a default`() {
        val run = PromotionRunFixtures.testPromotionRun(description = "My description")
        val text = source.render(
            entity = run,
            config = TemplatingSourceConfig.fromMap("default" to "Other description"),
            renderer = PlainEventRenderer.INSTANCE
        )
        assertEquals("My description", text)
    }

}