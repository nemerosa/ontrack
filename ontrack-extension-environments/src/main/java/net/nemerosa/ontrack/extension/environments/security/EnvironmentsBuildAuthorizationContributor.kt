package net.nemerosa.ontrack.extension.environments.security

import net.nemerosa.ontrack.extension.environments.EnvironmentsLicense
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class EnvironmentsBuildAuthorizationContributor(
    private val environmentsLicense: EnvironmentsLicense,
    private val securityService: SecurityService,
) : AuthorizationContributor {

    override fun appliesTo(context: Any): Boolean = context is Build

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> {
        val build = context as Build
        val environmentFeatureEnabled = environmentsLicense.environmentFeatureEnabled
        return listOf(
            Authorization(
                name = "slotPipeline",
                action = Authorization.CREATE,
                authorized = environmentFeatureEnabled &&
                        securityService.isProjectFunctionGranted<SlotPipelineCreate>(build)
            ),
        )
    }

}