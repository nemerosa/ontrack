package net.nemerosa.ontrack.boot.resources

import net.nemerosa.ontrack.ui.resource.AbstractResourceDecoratorTestSupport
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class AccountResourceDecoratorIT : AbstractResourceDecoratorTestSupport() {

    @Autowired
    private lateinit var accountResourceDecorator: AccountResourceDecorator

    @Test
    fun `Token links`() {
        asAdmin {
            val account = doCreateAccount()
            account.decorate(accountResourceDecorator) {
                assertLinkPresent("_revokeToken")
                assertLinkPresent("_generateToken")
                assertLinkPresent("_token")
            }
        }
    }
}