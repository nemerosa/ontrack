package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.browser.pages.ValidationStampPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.ValidationStamp
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.login
import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Regression test for #678
 */
@AcceptanceTestSuite
class ACCBrowserValidationBulkUpdateLink extends AcceptanceTestClient {

    private final String groupName = "ValidationManagers"
    private String username
    private final String password = "xxxx"
    private ValidationStamp vs

    @Before
    void prepare() {
        // Validation stamp to bulk update
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B') {
                validationStamp('VS')
            }
        }
        vs = ontrack.validationStamp(projectName, 'B', 'VS')

        // Validation global manager
        def group = ontrack.admin.accountGroup(groupName, "Global validation managers")
        ontrack.admin.setAccountGroupGlobalPermission(groupName, "GLOBAL_VALIDATION_MANAGER")

        username = uid("U")
        ontrack.admin.account(username, username, "dcoraboeuf@nemerosa.net", password, [groupName])
    }

    @Test
    void 'Validation bulk update accessible to global validation manager'() {
        browser { browser ->
            // Logs in
            login(browser, username, password, username)
            // Goes to the validation stamp page
            ValidationStampPage vsPage = goTo(ValidationStampPage, [id: vs.id])
            // Checks the "Bulk update" command
            def bulkUpdate = vsPage.bulkUpdateCommand
            assert bulkUpdate != null && bulkUpdate.isDisplayed(): "Bulk update command is present"
        }

    }

    @Test
    void 'Validation bulk update accessible to administrator'() {
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the validation stamp page
            ValidationStampPage vsPage = goTo(ValidationStampPage, [id: vs.id])
            // Checks the "Bulk update" command
            def bulkUpdate = vsPage.bulkUpdateCommand
            assert bulkUpdate != null && bulkUpdate.isDisplayed(): "Bulk update command is present"
        }

    }

}
