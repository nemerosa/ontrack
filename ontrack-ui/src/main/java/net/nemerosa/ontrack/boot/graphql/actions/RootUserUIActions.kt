package net.nemerosa.ontrack.boot.graphql.actions

import net.nemerosa.ontrack.boot.ui.ProjectController
import net.nemerosa.ontrack.graphql.schema.ProjectMutations
import net.nemerosa.ontrack.graphql.schema.RootUser
import net.nemerosa.ontrack.graphql.schema.actions.*
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isGlobalFunctionGranted
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import kotlin.reflect.KClass

@Component
class RootUserUIActions(
        uriBuilder: URIBuilder,
        securityService: SecurityService
) : UIActionsProvider<RootUser> {
    override val targetType: KClass<RootUser> = RootUser::class
    override val actions: List<UIAction<RootUser>> = listOf(
            UIAction(
                    ProjectMutations.CREATE_PROJECT,
                    "Creating a project",
                    listOf(
                            UIActionLink(
                                    UIActionLinks.CREATE,
                                    "Form to create a project",
                                    HttpMethod.GET
                            ) {
                                uriBuilder.build(
                                        on(ProjectController::class.java).newProjectForm()
                                ).takeIf {
                                    securityService.isGlobalFunctionGranted<ProjectCreation>()
                                }
                            }
                    ),
                    UIActionMutation(
                            ProjectMutations.CREATE_PROJECT
                    ) { securityService.isGlobalFunctionGranted<ProjectCreation>() }
            )
    )
}