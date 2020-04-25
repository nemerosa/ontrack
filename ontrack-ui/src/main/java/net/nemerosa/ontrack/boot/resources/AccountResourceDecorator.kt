package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.AccountController
import net.nemerosa.ontrack.boot.ui.TokensController
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.concurrent.TimeUnit

@Component
class AccountResourceDecorator : AbstractResourceDecorator<Account>(Account::class.java) {

    override fun links(account: Account, resourceContext: ResourceContext): List<Link> {
        return resourceContext.links()
                // Self
                .self(on(AccountController::class.java).getAccount(account.id))
                // Update
                .link(Link.UPDATE, on(AccountController::class.java).getUpdateForm(account.id))
                // Delete
                .link(Link.DELETE, on(AccountController::class.java).deleteAccount(account.id), !account.isDefaultAdmin)
                // Revoke any token
                .link(
                        "_revokeToken",
                        on(TokensController::class.java).revokeForAccount(account.id()),
                        AccountManagement::class.java
                )
                // Generates a token
                .link(
                        "_generateToken",
                        on(TokensController::class.java).generateForAccount(account.id(), TokensController.TokenGenerationRequest(0, TimeUnit.DAYS)),
                        AccountManagement::class.java
                )
                // Gets any token
                .link(
                        "_token",
                        on(TokensController::class.java).getTokenForAccount(account.id()),
                        AccountManagement::class.java
                )
                // OK
                .build()
    }

}