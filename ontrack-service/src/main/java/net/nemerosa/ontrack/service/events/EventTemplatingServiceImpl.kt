package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.springframework.stereotype.Service

@Service
class EventTemplatingServiceImpl(
    private val eventVariableService: EventVariableService,
    private val templatingService: TemplatingService,
) : EventTemplatingService {

    override fun renderEvent(
        event: Event,
        context: Map<String, Any>,
        template: String?,
        renderer: EventRenderer
    ): String =
        render(
            template = template?.takeIf { it.isNotBlank() } ?: event.eventType.template,
            event = event,
            context = context,
            renderer = renderer,
        )

    override fun render(template: String, event: Event, context: Map<String, Any>, renderer: EventRenderer): String {
        val templateContext = eventVariableService.getTemplateContext(event, context)
        return templatingService.render(
            template = template,
            context = templateContext,
            renderer = renderer,
        )
    }

}