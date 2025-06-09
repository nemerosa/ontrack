package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.ValidationStampFilterController
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.ValidationStampFilterCreate
import net.nemerosa.ontrack.model.security.ValidationStampFilterMgt
import net.nemerosa.ontrack.model.security.ValidationStampFilterShare
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.model.structure.ValidationStampFilterScope
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ValidationStampFilterResourceDecorator : AbstractLinkResourceDecorator<ValidationStampFilter>(ValidationStampFilter::class.java) {

    private fun canUpdate(resource: ValidationStampFilter, resourceContext: ResourceContext) = when {
        resource.project != null -> resourceContext.isProjectFunctionGranted(resource.project, ValidationStampFilterMgt::class.java)
        resource.branch != null -> resourceContext.isProjectFunctionGranted(resource.branch, ValidationStampFilterCreate::class.java)
        else -> resourceContext.isGlobalFunctionGranted(GlobalSettings::class.java)
    }

    override fun getLinkDefinitions(): Iterable<LinkDefinition<ValidationStampFilter>> = listOf(
            Link.DELETE linkTo { resource: ValidationStampFilter ->
                on(ValidationStampFilterController::class.java).deleteValidationStampFilter(resource.id)
            } linkIf ::canUpdate,
            "_shareAtProject" linkTo { resource: ValidationStampFilter ->
                on(ValidationStampFilterController::class.java).shareValidationStampFilterAtProject(resource.id)
            } linkIf { resource: ValidationStampFilter, resourceContext: ResourceContext ->
                resource.scope == ValidationStampFilterScope.BRANCH &&
                        resourceContext.isProjectFunctionGranted(resource.branch, ValidationStampFilterShare::class.java)
            },
            "_shareAtGlobal" linkTo { resource: ValidationStampFilter ->
                on(ValidationStampFilterController::class.java).shareValidationStampFilterAtGlobal(resource.id)
            } linkIf { resource: ValidationStampFilter, resourceContext: ResourceContext ->
                (resource.scope == ValidationStampFilterScope.PROJECT || resource.scope == ValidationStampFilterScope.BRANCH)
                        && resourceContext.isGlobalFunctionGranted(GlobalSettings::class.java)
            },
    )

}
