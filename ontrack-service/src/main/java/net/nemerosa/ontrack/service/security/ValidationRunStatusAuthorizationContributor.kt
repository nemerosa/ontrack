package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AuthenticatedUser
import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRunStatus
import org.springframework.stereotype.Component

@Component
class ValidationRunStatusAuthorizationContributor(
    private val structureService: StructureService,
) : AuthorizationContributor {

    companion object {
        const val VALIDATION_RUN_STATUS = "validation_run_status"
    }

    override fun appliesTo(context: Any): Boolean = context is ValidationRunStatus

    override fun getAuthorizations(user: AuthenticatedUser, context: Any): List<Authorization> {
        val status = context as ValidationRunStatus
        return listOf(
            Authorization(
                VALIDATION_RUN_STATUS,
                "comment_change",
                structureService.isValidationRunStatusCommentEditable(status.id)
            )
        )
    }
}