package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import org.springframework.stereotype.Component

@Component
class ProjectIndicatorTypeResourceDecorator : AbstractLinkResourceDecorator<ProjectIndicatorType>(ProjectIndicatorType::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<ProjectIndicatorType>> = listOf(

            // TODO Update
            // TODO Delete

    )

}