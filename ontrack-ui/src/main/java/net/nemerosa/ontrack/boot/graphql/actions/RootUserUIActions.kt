package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.ProjectController
import net.nemerosa.ontrack.graphql.schema.ProjectMutations
import net.nemerosa.ontrack.graphql.schema.RootUser
import net.nemerosa.ontrack.graphql.schema.actions.UIAction
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class RootUserUIActions(
        uriBuilder: URIBuilder,
        securityService: SecurityService
) : SimpleUIActionsProvider<RootUser>(RootUser::class, uriBuilder) {
    override val actions: List<UIAction<RootUser>> = listOf(
            mutationForm(ProjectMutations.CREATE_PROJECT, "Creating a project",
                    form = { on(ProjectController::class.java).newProjectForm() },
                    check = { securityService.isGlobalFunctionGranted<ProjectCreation>() }
            )
    )
}