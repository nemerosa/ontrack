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

    private val extensions: Collection<AuthorizationContributorExtension> by lazy {
        extensionManager.getExtensions(AuthorizationContributorExtension::class.java)
    }

    override fun appliesTo(context: Any): Boolean = true

    override fun getAuthorizations(user: OntrackAuthenticatedUser, context: Any): List<Authorization> =
        extensions.flatMap { it.getAuthorizations(user, context) }

}