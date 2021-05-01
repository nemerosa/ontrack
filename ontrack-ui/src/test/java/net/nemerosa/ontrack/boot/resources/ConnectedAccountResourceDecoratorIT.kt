package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.model.security.ConnectedAccount
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecoratorTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class ConnectedAccountResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var connectedAccountResourceDecorator: ConnectedAccountResourceDecorator

    @Test
    fun `Locked account has no link to change its password`() {
        val account = asAdmin {
            val initial = doCreateAccount()
            accountService.setAccountLocked(initial.id, true)
            accountService.getAccount(initial.id)
        }
        val connectedAccount = ConnectedAccount.of(account)
        connectedAccount.decorate(connectedAccountResourceDecorator) {
            assertLinkNotPresent("_changePassword")
        }
    }

}