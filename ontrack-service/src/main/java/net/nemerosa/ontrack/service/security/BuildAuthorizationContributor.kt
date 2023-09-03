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

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> = listOf(
        Authorization(BUILD, "promote", securityService.isProjectFunctionGranted<PromotionRunCreate>(context as Build))
    )
}