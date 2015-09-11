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

    @Test
    void 'Creation of account with groups'() {
        // Groups
        def g1Name = uid('G')
        def g2Name = uid('G')
        ontrack.admin.accountGroup(g1Name, "Group 1")
        ontrack.admin.accountGroup(g2Name, "Group 2")
        // Account
        def name = uid('A')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx", [g1Name, g2Name])
        // Checks it has been created
        def account = ontrack.admin.accounts.find { it.name == name }
        assert account != null
        assert account.fullName == "Damien Coraboeuf"
        assert account.email == "dcoraboeuf@nemerosa.net"
        assert account.authenticationSource.allowingPasswordChange
        assert account.authenticationSource.id == "password"
        assert account.authenticationSource.name == "Built-in"
        assert account.role == "USER"
        assert account.accountGroups.collect { it.name } == [g1Name, g2Name]
    }

}
