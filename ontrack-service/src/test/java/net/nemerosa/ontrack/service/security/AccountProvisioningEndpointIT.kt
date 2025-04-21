package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@AsAdminTest
class AccountProvisioningEndpointIT(
    @Autowired
    private val accountProvisioningEndpoint: AccountProvisioningEndpoint,
) : AbstractDSLTestSupport() {

    @Test
    fun `Provisioning a token for an existing user`() {
        val account = doCreateAccount()
        val token = accountProvisioningEndpoint.provisionAccount(username = account.name)
        assertTrue(
            token.isNotBlank(),
            "Token has been generated"
        )
    }

    @Test
    fun `Provisioning a token for a non existing user`() {
        assertFailsWith<IllegalStateException> {
            accountProvisioningEndpoint.provisionAccount(username = uid("user-"))
        }
    }

}