package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

@Component
class ValidationRunAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val VALIDATION_RUN = "validation_run"
    }

    override fun appliesTo(context: Any): Boolean = context is ValidationRun

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> {
        val run = context as ValidationRun
        return listOf(
            Authorization(
                VALIDATION_RUN,
                "status_change",
                securityService.isProjectFunctionGranted<ValidationRunStatusChange>(run)
            )
        )
    }
}