package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.extension.api.AuthorizationContributorExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.security.Authorization
import net.nemerosa.ontrack.model.security.AuthorizationContributor
import net.nemerosa.ontrack.model.security.OntrackAuthenticatedUser
import org.springframework.stereotype.Component

@Component
class ExtensionAuthorizationContributor(
    private val extensionManager: ExtensionManager,
) : AuthorizationContributor {
    override fun getAuthorizations(user: OntrackAuthenticatedUser): List<Authorization> =
        extensionManager.getExtensions(AuthorizationContributorExtension::class.java)
            .flatMap { it.getAuthorizations(user) }
}