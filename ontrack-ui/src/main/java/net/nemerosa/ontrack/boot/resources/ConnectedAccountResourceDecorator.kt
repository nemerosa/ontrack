package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.TokensController
import net.nemerosa.ontrack.boot.ui.UserController
import net.nemerosa.ontrack.model.security.ConnectedAccount
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class ConnectedAccountResourceDecorator : AbstractResourceDecorator<ConnectedAccount>(ConnectedAccount::class.java) {

    override fun links(account: ConnectedAccount, resourceContext: ResourceContext): List<Link> {
        return resourceContext.links()
                .self(on(UserController::class.java).currentUser)
                // Changing his password allowed for connected users which are built-in
                .link(
                        "_changePassword",
                        on(UserController::class.java).changePasswordForm,
                        account.isLogged && !account.account.locked && account.account.authenticationSource.isAllowingPasswordChange
                )
                // Getting the token value
                .link(
                        "_token",
                        on(TokensController::class.java).currentToken,
                        account.isLogged
                )
                // Changing the token value
                .link(
                        "_changeToken",
                        on(TokensController::class.java).generateNewToken(),
                        account.isLogged
                )
                // Changing the token value
                .link(
                        "_revokeToken",
                        on(TokensController::class.java).revokeToken(),
                        account.isLogged
                )
                // OK
                .build()
    }

}