package net.nemerosa.ontrack.boot.graphql

import net.nemerosa.ontrack.boot.ui.ProjectController
import net.nemerosa.ontrack.graphql.schema.GQLRootUserActionContributor
import net.nemerosa.ontrack.graphql.schema.URIDefinition
import net.nemerosa.ontrack.model.security.ProjectCreation
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.ui.controller.URIBuilder
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

/**
 * Main root actions.
 */
@Component
class DefaultGQLRootUserActionContributor constructor(
        private val uriBuilder: URIBuilder,
        private val securityService: SecurityService
) : GQLRootUserActionContributor {
    override val userRootActions: List<URIDefinition>
        get() = listOf(
                URIDefinition(
                        "projectCreate",
                        { securityService.isGlobalFunctionGranted(ProjectCreation::class.java) },
                        { uriBuilder.build(on(ProjectController::class.java).newProjectForm()) }
                )
        )
}
