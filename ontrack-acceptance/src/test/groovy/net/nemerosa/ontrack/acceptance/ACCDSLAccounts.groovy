package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the management of accounts
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
class ACCDSLAccounts extends AbstractACCDSL {

    @Test
    void 'Creation of accounts'() {
        // Account
        def name = uid('A')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        // Checks it has been created
        def account = ontrack.admin.accounts.find { it.name == name }
        assert account != null
        assert account.fullName == "Damien Coraboeuf"
        assert account.email == "dcoraboeuf@nemerosa.net"
        assert account.authenticationSource.allowingPasswordChange
        assert account.authenticationSource.id == "password"
        assert account.authenticationSource.name == "Built-in"
        assert account.role == "USER"
        assert account.accountGroups == []
    }

}
