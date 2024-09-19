package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class ProjectAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    override fun appliesTo(context: Any): Boolean = context is Project

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        (context as Project).let { project ->
            listOf(
                Authorization(
                    CoreAuthorizationContributor.PROJECT,
                    Authorization.CONFIG,
                    securityService.isProjectFunctionGranted<ProjectConfig>(project)
                ),
                Authorization(
                    CoreAuthorizationContributor.PROJECT,
                    Authorization.DISABLE,
                    securityService.isProjectFunctionGranted<ProjectDisable>(project)
                ),
            )
        }
}