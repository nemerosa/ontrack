package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.AccountController
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator
import net.nemerosa.ontrack.ui.resource.Link
import net.nemerosa.ontrack.ui.resource.LinkDefinition
import net.nemerosa.ontrack.ui.resource.linkTo
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class AccountResourceDecorator : AbstractLinkResourceDecorator<Account>(Account::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<Account>> = listOf(
        // Self
        Link.SELF linkTo { account, _ -> on(AccountController::class.java).getAccount(account.id) },
        // Update
        Link.UPDATE linkTo { account, _ -> on(AccountController::class.java).getUpdateForm(account.id) },
        // Delete
        Link.DELETE linkTo { account: Account, _ ->
            on(AccountController::class.java).deleteAccount(account.id)
        },
    )

}