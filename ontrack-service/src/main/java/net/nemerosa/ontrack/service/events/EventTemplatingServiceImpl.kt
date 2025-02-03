package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.common.SimpleExpand
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.EventVariableService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.templating.TemplatingService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EventTemplatingServiceImpl(
    private val eventVariableService: EventVariableService,
    private val templatingService: TemplatingService,
    private val securityService: SecurityService,
) : EventTemplatingService {

    private val logger = LoggerFactory.getLogger(javaClass)

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

    override fun render(template: String, event: Event, context: Map<String, Any>, renderer: EventRenderer): String =
        if (templatingService.isLegacyTemplate(template)) {
            val parameters = eventVariableService.getTemplateParameters(event, caseVariants = true)
            SimpleExpand.expand(template, parameters)
        } else {
            logger.debug(
                "Event template rendered with user={}",
                securityService.currentAccount?.account?.name,
            )
            val templateContext = eventVariableService.getTemplateContext(event, context)
            templatingService.render(
                template = template,
                context = templateContext,
                renderer = renderer,
            )
        }

}