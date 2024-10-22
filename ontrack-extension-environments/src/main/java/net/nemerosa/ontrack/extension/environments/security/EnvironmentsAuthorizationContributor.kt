package net.nemerosa.ontrack.extension.environments.security

import net.nemerosa.ontrack.extension.environments.environmentFeatureEnabled
import net.nemerosa.ontrack.extension.license.control.LicenseControlService
import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class EnvironmentsAuthorizationContributor(
    private val licenseControlService: LicenseControlService,
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        private const val CONTEXT = "environment"
    }

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> {
        val environmentFeatureEnabled = licenseControlService.environmentFeatureEnabled
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