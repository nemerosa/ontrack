package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.linkTo
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ProjectIndicatorResourceIndicator : AbstractLinkResourceDecorator<ProjectIndicator>(ProjectIndicator::class.java) {
    override fun getLinkDefinitions(): Iterable<LinkDefinition<ProjectIndicator>> = listOf(
            Link.UPDATE linkTo { i: ProjectIndicator ->
                on(IndicatorController::class.java).getUpdateFormForIndicator(
                        i.project.id,
                        i.type.id
                )
            }
    )
}