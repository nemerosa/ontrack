package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AuthenticatedUser
import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.model.structure.isUpdateAuthorized
import org.springframework.stereotype.Component

@Component
class ValidationStampFilterAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val VALIDATION_STAMP_FILTER = "validation_stamp_filter"
    }

    override fun appliesTo(context: Any): Boolean = context is ValidationStampFilter

    override fun getAuthorizations(user: AuthenticatedUser, context: Any): List<Authorization> =
        (context as ValidationStampFilter).let { filter ->
            listOf(
                Authorization(
                    VALIDATION_STAMP_FILTER,
                    Authorization.EDIT,
                    securityService.isUpdateAuthorized(filter)
                ),
            )
        }
}