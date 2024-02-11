package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.common.SimpleExpand
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

    override fun renderEvent(event: Event, template: String?, renderer: EventRenderer): String =
        render(
            template = template?.takeIf { it.isNotBlank() } ?: event.eventType.template,
            event = event,
            renderer = renderer,
        )

    override fun render(template: String, event: Event, renderer: EventRenderer): String =
        if (templatingService.isLegacyTemplate(template)) {
            val parameters = eventVariableService.getTemplateParameters(event, caseVariants = true)
            SimpleExpand.expand(template, parameters)
        } else {
            val context = eventVariableService.getTemplateContext(event)
            templatingService.render(
                template = template,
                context = context,
                renderer = renderer,
            )
        }

}