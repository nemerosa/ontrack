package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.LabelController
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagement
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class LabelResourceDecorator : AbstractResourceDecorator<Label>(Label::class.java) {
    override fun links(label: Label, resourceContext: ResourceContext): List<Link> =
            resourceContext.links()
                    // Update
                    .link(
                            Link.UPDATE,
                            on(LabelController::class.java).getUpdateLabelForm(label.id),
                            label.computedBy == null && resourceContext.isGlobalFunctionGranted(LabelManagement::class.java)
                    )
                    // OK
                    .build()
}