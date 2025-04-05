package net.nemerosa.ontrack.extension.environments.security

import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class EnvironmentsAuthorizationContributor(
    private val environmentsLicense: EnvironmentsLicense,
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        private const val CONTEXT = "environment"
    }

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: AuthenticatedUser, context: Any): List<Authorization> {
        val environmentFeatureEnabled = environmentsLicense.environmentFeatureEnabled
        return listOf(
            Authorization(
                name = CONTEXT,
                action = Authorization.VIEW,
                authorized = environmentFeatureEnabled &&
                        securityService.isGlobalFunctionGranted<EnvironmentList>()
            ),
            Authorization(
                name = CONTEXT,
                action = Authorization.CREATE,
                authorized = environmentFeatureEnabled &&
                        securityService.isGlobalFunctionGranted<EnvironmentSave>()
            ),
            Authorization(
                name = CONTEXT,
                action = Authorization.EDIT,
                authorized = environmentFeatureEnabled &&
                        securityService.isGlobalFunctionGranted<EnvironmentSave>()
            ),
            Authorization(
                name = CONTEXT,
                action = Authorization.DELETE,
                authorized = environmentFeatureEnabled &&
                        securityService.isGlobalFunctionGranted<EnvironmentDelete>()
            ),
        )
    }

}