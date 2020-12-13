package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.ValidationRunController
import net.nemerosa.ontrack.graphql.schema.ValidationRunMutations
import net.nemerosa.ontrack.graphql.schema.actions.UIAction
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.ValidationRunCreate
import net.nemerosa.ontrack.model.security.isProjectFunctionGranted
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

@Component
class BuildUIActions(
    uriBuilder: URIBuilder,
    private val securityService: SecurityService
) : SimpleUIActionsProvider<Build>(Build::class, uriBuilder) {
    override val actions: List<UIAction<Build>> = listOf(
        mutationForm(
            ValidationRunMutations.CREATE_VALIDATION_RUN_FOR_BUILD_BY_ID, "Creating a validation run for this build",
            form = { build ->
                MvcUriComponentsBuilder.on(ValidationRunController::class.java).newValidationRunForm(build.id)
            },
            check = { build -> securityService.isProjectFunctionGranted<ValidationRunCreate>(build) }
        )
    )
}