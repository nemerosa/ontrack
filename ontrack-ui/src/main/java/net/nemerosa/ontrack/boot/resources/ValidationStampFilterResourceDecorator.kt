package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.ValidationStampFilterController
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.model.structure.ValidationStampFilterScope
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ValidationStampFilterResourceDecorator : AbstractResourceDecorator<ValidationStampFilter>(ValidationStampFilter::class.java) {

    override fun links(resource: ValidationStampFilter, resourceContext: ResourceContext): List<Link> {
        // Scope of the validation stamp filter
        val canUpdate: Boolean = when {
            resource.project != null -> resourceContext.isProjectFunctionGranted(resource.project, ValidationStampFilterMgt::class.java)
            resource.branch != null -> resourceContext.isProjectFunctionGranted(resource.branch, ValidationStampFilterCreate::class.java)
            else -> resourceContext.isGlobalFunctionGranted(GlobalSettings::class.java)
        }
        // Links
        return resourceContext.links()
                // Update if authorized
                .link(
                        Link.UPDATE,
                        on(ValidationStampFilterController::class.java).getValidationStampFilterUpdateForm(resource.id),
                        canUpdate
                )
                // Delete if authorized
                .link(
                        Link.DELETE,
                        on(ValidationStampFilterController::class.java).deleteValidationStampFilter(resource.id),
                        canUpdate
                )
                // Share at project level
                .link(
                        "_shareAtProject",
                        on(ValidationStampFilterController::class.java).shareValidationStampFilterAtProject(resource.id),
                        resource.scope == ValidationStampFilterScope.BRANCH && resourceContext.isProjectFunctionGranted(resource.branch, ValidationStampFilterShare::class.java)
                )
                // Share at global level
                .link(
                        "_shareAtGlobal",
                        on(ValidationStampFilterController::class.java).shareValidationStampFilterAtGlobal(resource.id),
                        (resource.scope == ValidationStampFilterScope.PROJECT || resource.scope == ValidationStampFilterScope.BRANCH) && resourceContext.isGlobalFunctionGranted(GlobalSettings::class.java)
                )
                // OK
                .build()
    }
}
