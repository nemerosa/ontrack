package net.nemerosa.ontrack.service.events

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import net.nemerosa.ontrack.model.structure.EntityDisplayNameService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.defaultDisplayName
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.templating.TemplatingService
import net.nemerosa.ontrack.service.templating.TemplatingServiceImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventTemplatingServiceImplTest {

    private lateinit var eventVariableService: EventVariableService
    private lateinit var templatingService: TemplatingService
    private lateinit var eventTemplatingService: EventTemplatingService
    private lateinit var entityDisplayNameService: EntityDisplayNameService

    @BeforeEach
    fun init() {
        eventVariableService = mockk()
        every { eventVariableService.getTemplateContext(any(), any()) } returns mapOf(
            "branch" to "release/1.27"
        )

        entityDisplayNameService = mockk()
        every { entityDisplayNameService.getEntityDisplayName(any()) } answers {
            val entity = it.invocation.args.first() as ProjectEntity
            entity.defaultDisplayName
        }

        templatingService = TemplatingServiceImpl(
            templatingSources = emptyList(),
            templatingFilters = emptyList(),
            templatingFunctions = emptyList(),
            templatingContextHandlers = emptyList(),
            ontrackConfigProperties = OntrackConfigProperties(),
            entityDisplayNameService = entityDisplayNameService,
        )
        eventTemplatingService = EventTemplatingServiceImpl(
            eventVariableService,
            templatingService,
        )
    }

    @Test
    fun `Render event for new syntax`() {
        val event = mockk<Event>()
        val text = eventTemplatingService.renderEvent(
            event = event,
            context = emptyMap(),
            template = "New templated ${'$'}{branch} branch",
            renderer = PlainEventRenderer.INSTANCE,
        )
        assertEquals("New templated release/1.27 branch", text)
    }

}