package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.http.OTForbiddenClientException
import net.nemerosa.ontrack.dsl.v4.http.OTMessageClientException
import org.junit.Ignore
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the management of accounts
 */
@AcceptanceTestSuite
class ACCDSLAccounts extends AbstractACCDSL {

    @Test
    void 'Disabling an account'() {
        // Account
        def name = uid('A')
        def account = ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        assert !account.disabled: "Account is not disabled"
        // Disabling the account
        account.disable()
        // Checks it's disabled
        ontrack.admin.findAccountById(account.id).with {
            assert it.disabled: "Account is disabled"
        }
        // Enabling the account
        account.enable()
        // Checks it's not disabled any longer
        ontrack.admin.findAccountById(account.id).with {
            assert !it.disabled: "Account is enabled"
        }
    }

    @Test
    void 'Locking an account'() {
        // Account
        def name = uid('A')
        def account = ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        assert !account.locked: "Account is not locked"
        // Locking the account
        account.lock()
        // Checks it's locked
        ontrack.admin.findAccountById(account.id).with {
            assert it.locked: "Account is locked"
        }
        // Unlocking the account
        account.unlock()
        // Checks it's not locked any longer
        ontrack.admin.findAccountById(account.id).with {
            assert !it.locked: "Account is unlocked"
        }
    }

    @Test
    void 'Creation of accounts'() {
        // Account
        def name = uid('A')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        // Checks it has been created
        def account = ontrack.admin.accounts.find { it.name == name }
        assert account != null
        assert account.name == name
        assert account.fullName == "Damien Coraboeuf"
        assert account.email == "dcoraboeuf@nemerosa.net"
        assert account.authenticationSource.allowingPasswordChange
        assert account.authenticationSource.provider == "built-in"
        assert account.authenticationSource.key == ""
        assert account.role == "USER"
        assert account.accountGroups == []
    }

    @Test
    void 'Creation of account with a dot in its name'() {
        // Account
        def name = uid('A.B')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        // Checks it has been created
        def account = ontrack.admin.accounts.find { it.name == name }
        assert account != null
        assert account.name == name
        assert account.fullName == "Damien Coraboeuf"
        assert account.email == "dcoraboeuf@nemerosa.net"
        assert account.authenticationSource.allowingPasswordChange
        assert account.authenticationSource.provider == "built-in"
        assert account.authenticationSource.key == ""
        assert account.role == "USER"
        assert account.accountGroups == []
    }

    @Test
    void 'Creation of account with a @ in its name'() {
        // Account
        def name = "damien.${uid('N')}@nemerosa.net"
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        // Checks it has been created
        def account = ontrack.admin.accounts.find { it.name == name }
        assert account != null
        assert account.name == name
        assert account.fullName == "Damien Coraboeuf"
        assert account.email == "dcoraboeuf@nemerosa.net"
        assert account.authenticationSource.allowingPasswordChange
        assert account.authenticationSource.provider == "built-in"
        assert account.authenticationSource.key == ""
        assert account.authenticationSource.name == "Built-in"
        assert account.role == "USER"
        assert account.accountGroups == []
    }

    @Test(expected = OTMessageClientException.class)
    void 'Creation of account with an invalid character in its name'() {
        // Account
        def name = uid('A/B')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
    }

    @Test
    void 'Creation of account with a dash in its name'() {
        // Account
        def name = uid('A-B')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        // Checks it has been created
        def account = ontrack.admin.accounts.find { it.name == name }
        assert account != null
        assert account.fullName == "Damien Coraboeuf"
        assert account.email == "dcoraboeuf@nemerosa.net"
        assert account.authenticationSource.allowingPasswordChange
        assert account.authenticationSource.provider == "built-in"
        assert account.authenticationSource.key == ""
        assert account.role == "USER"
        assert account.accountGroups == []
    }

    @Test
    void 'Creation of account with an underscore in its name'() {
        // Account
        def name = uid('A_B')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net", "xxxx")
        // Checks it has been created
        def account = ontrack.admin.accounts.find { it.name == name }
        assert account != null
        assert account.fullName == "Damien Coraboeuf"
        assert account.email == "dcoraboeuf@nemerosa.net"
        assert account.authenticationSource.allowingPasswordChange
        assert account.authenticationSource.provider == "built-in"
        assert account.authenticationSource.key == ""
        assert account.authenticationSource.name == "Built-in"
        assert account.role == "USER"
        assert account.accountGroups == []
    }

    @Test
    @Ignore("#756 Refactoring of security")
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

    @Test
    void 'Account global permissions'() {
        // Account
        def name = uid('A')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net")
        // Sets permissions
        ontrack.admin.setAccountGlobalPermission(name, "ADMINISTRATOR")
        // Checks its global permissions
        def permissions = ontrack.admin.getAccountGlobalPermissions(name)
        assert permissions != null
        assert permissions.size() == 1
        assert permissions[0].id == 'ADMINISTRATOR'
        assert permissions[0].name == 'Administrator'
        assert permissions[0].description == "An administrator is allowed to do everything in the application."
    }

    @Test
    void 'Account project permissions'() {
        // Project
        def project = uid('P')
        ontrack.project(project)
        // Account
        def name = uid('A')
        ontrack.admin.account(name, "Damien Coraboeuf", "dcoraboeuf@nemerosa.net")
        // Sets permissions
        ontrack.admin.setAccountProjectPermission(project, name, "OWNER")
        // Checks the project permissions
        def permissions = ontrack.admin.getAccountProjectPermissions(project, name)
        assert permissions != null
        assert permissions.size() == 1
        assert permissions[0].id == 'OWNER'
        assert permissions[0].name == 'Project owner'
        assert permissions[0].description == "The project owner is allowed to all functions in a project, but for its deletion."
    }

    @Test
    void 'Account group global permissions'() {
        // Group
        def name = uid('G')
        ontrack.admin.accountGroup(name, "Test group")
        // Sets permissions
        ontrack.admin.setAccountGroupGlobalPermission(name, "ADMINISTRATOR")
        // Checks its global permissions
        def permissions = ontrack.admin.getAccountGroupGlobalPermissions(name)
        assert permissions != null
        assert permissions.size() == 1
        assert permissions[0].id == 'ADMINISTRATOR'
        assert permissions[0].name == 'Administrator'
        assert permissions[0].description == "An administrator is allowed to do everything in the application."
    }

    @Test
    void 'Account group project permissions'() {
        // Project
        def project = uid('P')
        ontrack.project(project)
        // Group
        def name = uid('G')
        ontrack.admin.accountGroup(name, "Test")
        // Sets permissions
        ontrack.admin.setAccountGroupProjectPermission(project, name, "PARTICIPANT")
        // Checks the project permissions
        def permissions = ontrack.admin.getAccountGroupProjectPermissions(project, name)
        assert permissions != null
        assert permissions.size() == 1
        assert permissions[0].id == 'PARTICIPANT'
        assert permissions[0].name == 'Participant'
        assert permissions[0].description == "A participant in a project is allowed to change statuses in validation runs."
    }

    @Test
    void 'Automation role can create groups'() {
        // Automation account
        def userName = uid('A')
        doCreateAutomation(userName, 'pwd')
        ontrack = getOntrackAs(userName, 'pwd')
        // Creating a group
        def group = ontrack.admin.accountGroup(uid('G'), "Test group")
        assert group != null
    }

    @Test(expected = OTForbiddenClientException)
    void 'Automation role cannot create accounts'() {
        // Automation account
        def userName = uid('A')
        doCreateAutomation(userName, 'pwd')
        ontrack = getOntrackAs(userName, 'pwd')
        // Creating an account
        ontrack.admin.account(uid('A'), uid('N'), "test@test.com", "pwd")
    }

    @Test
    void 'Automation role can set project permissions'() {
        // Creating a project
        def projectName = doCreateProject().name.asText() as String
        // Creating a group
        String groupName = uid('G')
        ontrack.admin.accountGroup(groupName, "Test group")
        // Automation account
        def userName = uid('A')
        doCreateAutomation(userName, 'pwd')
        ontrack = getOntrackAs(userName, 'pwd')
        // Project permission for this group
        ontrack.admin.setAccountGroupProjectPermission(projectName, groupName, 'PARTICIPANT')
    }

}
