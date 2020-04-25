package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.AccountController
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.ResourceContext
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

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
                // OK
                .build()
    }

}