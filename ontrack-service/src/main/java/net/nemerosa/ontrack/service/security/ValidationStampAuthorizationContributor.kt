package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.stereotype.Component

@Component
class ValidationStampAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val VALIDATION_STAMP = "validation_stamp"
    }

    override fun appliesTo(context: Any): Boolean = context is ValidationStamp

    override fun getAuthorizations(user: AuthenticatedUser, context: Any): List<Authorization> = listOf(
        Authorization(
            VALIDATION_STAMP,
            Authorization.EDIT,
            securityService.isProjectFunctionGranted<ValidationStampEdit>(context as ValidationStamp)
        ),
        Authorization(
            VALIDATION_STAMP,
            Authorization.DELETE,
            securityService.isProjectFunctionGranted<ValidationStampDelete>(context)
        ),
    )
}