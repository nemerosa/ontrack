package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.Branch
import org.springframework.stereotype.Component

@Component
class BranchAuthorizationContributor(
    private val securityService: SecurityService,
) : AuthorizationContributor {

    companion object {
        const val BRANCH = "branch"
    }

    override fun appliesTo(context: Any): Boolean = context is Branch

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        (context as Branch).let { branch ->
            listOf(
                Authorization(
                    BRANCH,
                    "build_filter_manage",
                    securityService.isProjectFunctionGranted<BranchFilterMgt>(branch)
                ),
                Authorization(
                    BRANCH,
                    "validation_stamp_filter_create",
                    securityService.isProjectFunctionGranted<ValidationStampFilterCreate>(branch)
                ),
                Authorization(
                    BRANCH,
                    "validation_stamp_filter_share",
                    securityService.isProjectFunctionGranted<ValidationStampFilterShare>(branch)
                ),
            )
        }
}