package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.extension.indicators.computing.ConfigurableIndicatorTypeState
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.linkTo
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ConfigurableIndicatorTypeStateResourceDecorator :
    AbstractLinkResourceDecorator<ConfigurableIndicatorTypeState<*, *>>(ConfigurableIndicatorTypeState::class.java) {
    override fun getLinkDefinitions(): List<LinkDefinition<ConfigurableIndicatorTypeState<*, *>>> = listOf(
        Link.UPDATE.linkTo { t: ConfigurableIndicatorTypeState<*, *> ->
            on(ConfigurableIndicatorController::class.java).getEditionForm(t.type.id)
        }
    )
}