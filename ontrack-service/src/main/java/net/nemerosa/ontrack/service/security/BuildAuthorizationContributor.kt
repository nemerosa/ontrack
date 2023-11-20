package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Component

@Component
class BuildAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val BUILD = "build"
    }

    override fun appliesTo(context: Any): Boolean = context is Build

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        (context as Build).let { build ->
            listOf(
                Authorization(
                    BUILD,
                    "promote",
                    securityService.isProjectFunctionGranted<PromotionRunCreate>(build)
                ),
                Authorization(
                    BUILD,
                    "validate",
                    securityService.isProjectFunctionGranted<ValidationRunCreate>(build)
                ),
            )
        }
}