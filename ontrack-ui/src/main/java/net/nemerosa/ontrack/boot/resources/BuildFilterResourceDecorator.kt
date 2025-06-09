package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.BuildFilterController
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.linkTo
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class BuildFilterResourceDecorator : AbstractLinkResourceDecorator<BuildFilterResource<*>>(BuildFilterResource::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<BuildFilterResource<*>>> = listOf(
            // Deleting
            Link.DELETE linkTo { filter ->
                on(BuildFilterController::class.java).deleteFilter(filter.branch.id, filter.name)
            },
    )

}