package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.ValidationRunStatusController
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRunStatus
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.LinkDefinitions.link
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ValidationRunStatusResourceDecorator(
        private val structureService: StructureService
) : AbstractLinkResourceDecorator<ValidationRunStatus>(
        ValidationRunStatus::class.java
) {
    override fun getLinkDefinitions(): Iterable<LinkDefinition<ValidationRunStatus>> =
            listOf(
                    link(
                            "_comment",
                            { status, _ -> on(ValidationRunStatusController::class.java).getValidationRunStatusEditCommentForm(status.id) },
                            { status, _ -> structureService.isValidationRunStatusCommentEditable(status.id) }
                    )
            )
}