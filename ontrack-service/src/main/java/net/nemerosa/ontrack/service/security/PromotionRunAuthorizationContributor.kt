package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.springframework.stereotype.Component

@Component
class PromotionRunAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val PROMOTION_RUN = "promotion_run"
    }

    override fun appliesTo(context: Any): Boolean = context is PromotionRun

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> = listOf(
        Authorization(
            PROMOTION_RUN,
            Authorization.DELETE,
            securityService.isProjectFunctionGranted<PromotionRunDelete>(context as PromotionRun)
        ),
        Authorization(
            BuildAuthorizationContributor.BUILD,
            BuildAuthorizationContributor.ACTION_PROMOTE,
            securityService.isProjectFunctionGranted<PromotionRunCreate>(context)
        )
    )
}