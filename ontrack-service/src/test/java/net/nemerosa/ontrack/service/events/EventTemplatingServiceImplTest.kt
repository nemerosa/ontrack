package net.nemerosa.ontrack.service.events

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.templating.TemplatingService
import net.nemerosa.ontrack.service.templating.TemplatingServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventTemplatingServiceImplTest {

    private lateinit var eventVariableService: EventVariableService
    private lateinit var templatingService: TemplatingService
    private lateinit var eventTemplatingService: EventTemplatingService

    @BeforeEach
    fun init() {
        eventVariableService = mockk()
        every { eventVariableService.getTemplateContext(any()) } returns mapOf(
            "branch" to "release/1.27"
        )
        every { eventVariableService.getTemplateParameters(any(), caseVariants = true) } returns mapOf(
            "branch" to "release/1.27"
        )

        templatingService = TemplatingServiceImpl(
            templatingSources = emptyList(),
            templatingFilters = emptyList(),
        )
        eventTemplatingService = EventTemplatingServiceImpl(
            eventVariableService,
            templatingService,
        )
    }

    @Test
    fun `Render event for legacy`() {
        val event = mockk<Event>()
        val text = eventTemplatingService.renderEvent(
            event = event,
            template = "Legacy {branch} branch",
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("Legacy release/1.27 branch", text)
    }

    @Test
    fun `Render event for new syntax`() {
        val event = mockk<Event>()
        val text = eventTemplatingService.renderEvent(
            event = event,
            template = "New templated ${'$'}{branch} branch",
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("New templated release/1.27 branch", text)
    }

}