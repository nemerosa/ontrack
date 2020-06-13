package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.ProjectController
import net.nemerosa.ontrack.graphql.schema.actions.*
import net.nemerosa.ontrack.graphql.schema.authorizations.isProjectFunctionGranted
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
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
                    "updateProject",
                    "Updating the project",
                    listOf(
                            UIActionLink(
                                    UIActionLinks.UPDATE,
                                    "Update form",
                                    HttpMethod.GET
                            ) { uriBuilder.build(on(ProjectController::class.java).saveProjectForm(it.id)) }
                    ),
                    UIActionMutation(
                            "updateProject"
                    ) { securityService.isProjectFunctionGranted<ProjectEdit>(it) }
            )
    )
}