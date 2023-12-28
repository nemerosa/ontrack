package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.springframework.stereotype.Component

@Component
class PromotionLevelAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val PROMOTION_LEVEL = "promotion_level"
    }

    override fun appliesTo(context: Any): Boolean = context is PromotionLevel

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> = listOf(
        Authorization(
            PROMOTION_LEVEL,
            Authorization.EDIT,
            securityService.isProjectFunctionGranted<PromotionLevelEdit>(context as PromotionLevel)
        ),
        Authorization(
            PROMOTION_LEVEL,
            Authorization.DELETE,
            securityService.isProjectFunctionGranted<PromotionLevelDelete>(context)
        ),
    )
}