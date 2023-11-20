package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class CoreAuthorizationContributor : AuthorizationContributor {

    companion object {
        const val PROJECT = "project"
    }

    override fun appliesTo(context: Any): Boolean = context is GlobalAuthorizationContext

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        listOf(
            // Project
            Authorization(PROJECT, Authorization.CREATE, user.isGranted(ProjectCreation::class.java)),
        )

}