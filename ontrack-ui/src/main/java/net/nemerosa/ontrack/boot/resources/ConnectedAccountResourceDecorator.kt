package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.UserController
import net.nemerosa.ontrack.model.security.ConnectedAccount
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

@Component
class ConnectedAccountResourceDecorator : AbstractResourceDecorator<ConnectedAccount>(ConnectedAccount::class.java) {

    override fun links(account: ConnectedAccount, resourceContext: ResourceContext): List<Link> {
        return resourceContext.links()
                .self(MvcUriComponentsBuilder.on(UserController::class.java).currentUser)
                // Changing his password allowed for connected users which are built-in
                .link(
                        "_changePassword",
                        MvcUriComponentsBuilder.on(UserController::class.java).changePasswordForm,
                        account.isLogged && account.account.authenticationSource.isAllowingPasswordChange
                )
                // OK
                .build()
    }

}