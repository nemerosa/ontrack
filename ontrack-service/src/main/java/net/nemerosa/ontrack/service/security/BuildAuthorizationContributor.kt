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

        const val ACTION_PROMOTE = "promote"
        const val ACTION_VALIDATE = "validate"
    }

    override fun appliesTo(context: Any): Boolean = context is Build

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        (context as Build).let { build ->
            listOf(
                Authorization(
                    BUILD,
                    ACTION_PROMOTE,
                    securityService.isProjectFunctionGranted<PromotionRunCreate>(build)
                ),
                Authorization(
                    BUILD,
                    ACTION_VALIDATE,
                    securityService.isProjectFunctionGranted<ValidationRunCreate>(build)
                ),
            )
        }
}