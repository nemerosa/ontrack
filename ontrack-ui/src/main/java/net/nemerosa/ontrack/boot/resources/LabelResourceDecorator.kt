package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.LabelController
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagement
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.LinkDefinitions
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class LabelResourceDecorator : AbstractLinkResourceDecorator<Label>(Label::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<Label>> =
            listOf(
                    LinkDefinitions.link<Label>(
                            Link.UPDATE,
                            { label, _ -> on(LabelController::class.java).getUpdateLabelForm(label.id) },
                            { label, resourceContext -> label.computedBy == null && resourceContext.isGlobalFunctionGranted(LabelManagement::class.java) }
                    )
            )
}
