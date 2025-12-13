package net.nemerosa.ontrack.extension.notifications.subscriptions

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class GlobalSubscriptionsAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val SUBSCRIPTIONS = "subscriptions"
    }

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: AuthenticatedUser, context: Any): List<Authorization> {
        return listOf(
            Authorization(
                SUBSCRIPTIONS,
                Authorization.VIEW,
                securityService.isGlobalFunctionGranted<GlobalSubscriptionsManage>()
            ),
            Authorization(
                SUBSCRIPTIONS,
                Authorization.EDIT,
                securityService.isGlobalFunctionGranted<GlobalSubscriptionsManage>()
            ),
        )
    }
}