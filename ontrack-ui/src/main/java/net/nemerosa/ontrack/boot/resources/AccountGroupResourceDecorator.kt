package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.boot.ui.AccountController
import net.nemerosa.ontrack.model.security.AccountGroup
import net.nemerosa.ontrack.model.security.AccountManagement
import net.nemerosa.ontrack.ui.resource.*
import org.springframework.stereotype.Component
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@Component
class AccountGroupResourceDecorator : AbstractLinkResourceDecorator<AccountGroup>(AccountGroup::class.java) {

    override fun getLinkDefinitions(): Iterable<LinkDefinition<AccountGroup>> = listOf(
            Link.SELF linkTo { group ->
                on(AccountController::class.java).getGroup(group.id)
            },

            Link.UPDATE linkTo { group: AccountGroup ->
                on(AccountController::class.java).getGroupUpdateForm(group.id)
            } linkIfGlobal AccountManagement::class,

            Link.DELETE linkTo { group: AccountGroup ->
                on(AccountController::class.java).deleteGroup(group.id)
            } linkIfGlobal AccountManagement::class
    )
    
}