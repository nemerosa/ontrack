package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.AccountController
import net.nemerosa.ontrack.boot.ui.TokensController
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on
import java.util.concurrent.TimeUnit

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
            } linkIf { account: Account, _ ->
                !account.isDefaultAdmin
            },
            // Revoke any token
            "_revokeToken" linkTo { account: Account ->
                on(TokensController::class.java).revokeForAccount(account.id())
            } linkIfGlobal (AccountManagement::class),
            // Generates a token
            "_generateToken" linkTo { account: Account ->
                on(TokensController::class.java).generateForAccount(account.id(), TokensController.TokenGenerationRequest(0, TimeUnit.DAYS))
            } linkIfGlobal (AccountManagement::class),
            // Gets any token
            "_token" linkTo { account: Account ->
                on(TokensController::class.java).getTokenForAccount(account.id())
            } linkIfGlobal (AccountManagement::class)
    )

}