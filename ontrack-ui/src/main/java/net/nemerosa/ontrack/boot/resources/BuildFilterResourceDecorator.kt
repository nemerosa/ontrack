package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.BuildFilterController
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource
import net.nemerosa.ontrack.model.security.BranchFilterMgt
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class BuildFilterResourceDecorator : AbstractLinkResourceDecorator<BuildFilterResource<*>>(BuildFilterResource::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<BuildFilterResource<*>>> = listOf(
            // Update
            Link.UPDATE linkTo { filter ->
                on(BuildFilterController::class.java).getEditionForm(filter.branch.id, filter.name)
            },
            // Sharing
            "_share" linkTo { filter: BuildFilterResource<*> ->
                on(BuildFilterController::class.java).getEditionForm(filter.branch.id, filter.name)
            } linkIf { filter: BuildFilterResource<*>, rc ->
                rc.isProjectFunctionGranted(filter.branch, BranchFilterMgt::class.java) && !filter.isShared
            },
            // Deleting
            Link.DELETE linkTo { filter ->
                on(BuildFilterController::class.java).deleteFilter(filter.branch.id, filter.name)
            },
    )

}