package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.ProjectController
import net.nemerosa.ontrack.graphql.schema.ProjectMutations
import net.nemerosa.ontrack.graphql.schema.actions.UIAction
import net.nemerosa.ontrack.graphql.schema.actions.UIActionLink
import net.nemerosa.ontrack.graphql.schema.actions.UIActionLinks
import net.nemerosa.ontrack.graphql.schema.actions.UIActionMutation
import net.nemerosa.ontrack.model.security.ProjectDelete
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isProjectFunctionGranted
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ProjectUIActions(
        uriBuilder: URIBuilder,
        private val securityService: SecurityService
) : SimpleUIActionsProvider<Project>(Project::class, uriBuilder) {

    override val actions: List<UIAction<Project>> = listOf(

            mutationForm(ProjectMutations.UPDATE_PROJECT, "Updating the project",
                    form = { on(ProjectController::class.java).saveProjectForm(it.id) },
                    check = { securityService.isProjectFunctionGranted<ProjectEdit>(it) }
            ),

            mutationOnly(ProjectMutations.DELETE_PROJECT, "Deleting the project") {
                securityService.isProjectFunctionGranted<ProjectDelete>(it)
            },

            mutationOnly(ProjectMutations.DISABLE_PROJECT, "Disabling the project") {
                securityService.isProjectFunctionGranted<ProjectEdit>(it)
            },

            mutationOnly(ProjectMutations.ENABLE_PROJECT, "Enabling the project") {
                securityService.isProjectFunctionGranted<ProjectEdit>(it)
            }
            // TODO Mark a project as favourite
            // TODO Unmark a project as favourite
    )
}