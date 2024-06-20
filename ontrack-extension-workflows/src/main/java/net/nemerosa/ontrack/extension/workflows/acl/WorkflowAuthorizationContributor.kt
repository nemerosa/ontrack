package net.nemerosa.ontrack.extension.workflows.acl

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class WorkflowAuthorizationContributor : AuthorizationContributor {

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        listOf(
            // Global settings
            Authorization(
                WORKFLOW,
                "stop",
                user.isGranted(WorkflowStop::class.java)
            ),
        )

    companion object {
        private const val WORKFLOW = "workflow"
    }

}