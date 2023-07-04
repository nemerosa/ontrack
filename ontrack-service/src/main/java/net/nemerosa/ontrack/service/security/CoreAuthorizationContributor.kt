package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import net.nemerosa.ontrack.model.security.ProjectCreation
import org.springframework.stereotype.Component

@Component
class CoreAuthorizationContributor: AuthorizationContributor {

    companion object {
        const val PROJECT = "project"
    }

    override fun getAuthorizations(user: OntrackAuthenticatedUser): List<Authorization> =
        listOf(
            // Project
            Authorization(PROJECT, Authorization.CREATE, user.isGranted(ProjectCreation::class.java)),
        )

}