package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.ProjectController
import net.nemerosa.ontrack.graphql.schema.ProjectMutations
import net.nemerosa.ontrack.graphql.schema.actions.*
import net.nemerosa.ontrack.model.security.ProjectDelete
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isProjectFunctionGranted
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import kotlin.reflect.KClass

@Component
class ProjectUIActions(
        private val uriBuilder: URIBuilder,
        private val securityService: SecurityService
) : UIActionsProvider<Project> {

    override val targetType: KClass<Project> = Project::class
    override val actions: List<UIAction<Project>> = listOf(
            UIAction(
                    ProjectMutations.UPDATE_PROJECT,
                    "Updating the project",
                    listOf(
                            UIActionLink(
                                    UIActionLinks.FORM,
                                    "Update form",
                                    HttpMethod.GET
                            ) { uriBuilder.build(on(ProjectController::class.java).saveProjectForm(it.id)) }
                    ),
                    UIActionMutation(
                            ProjectMutations.UPDATE_PROJECT
                    ) { securityService.isProjectFunctionGranted<ProjectEdit>(it) }
            ),
            UIAction(
                    ProjectMutations.DELETE_PROJECT,
                    "Deleting the project",
                    emptyList(),
                    UIActionMutation(
                            ProjectMutations.DELETE_PROJECT
                    ) { securityService.isProjectFunctionGranted<ProjectDelete>(it) }
            ),
            UIAction(
                    ProjectMutations.DISABLE_PROJECT,
                    "Disabling the project",
                    emptyList(),
                    UIActionMutation(
                            ProjectMutations.DISABLE_PROJECT
                    ) { securityService.isProjectFunctionGranted<ProjectEdit>(it) }
            ),
            UIAction(
                    ProjectMutations.ENABLE_PROJECT,
                    "Enabling the project",
                    emptyList(),
                    UIActionMutation(
                            ProjectMutations.ENABLE_PROJECT
                    ) { securityService.isProjectFunctionGranted<ProjectEdit>(it) }
            )
            // TODO Mark a project as favourite
            // TODO Unmark a project as favourite
    )
}