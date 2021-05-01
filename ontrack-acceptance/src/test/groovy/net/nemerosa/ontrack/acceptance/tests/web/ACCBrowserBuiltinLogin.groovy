package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.browser.pages.LoginPage
import net.nemerosa.ontrack.acceptance.steps.BasicSteps
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCBrowserBuiltinLogin extends AcceptanceTestClient {

    @Test
    void 'Account login'() {
        withAccount { username, password, fullName ->
            browser { browser ->
                BasicSteps.login(browser, username, password, fullName)
            }
        }
    }

    @Test
    void 'Disabled account cannot login'() {
        withAccount(true) { username, password, _ ->
            browser { browser ->
                browser.with {
                    def loginPage = goTo LoginPage, [:]
                    loginPage.login username, password
                    assert loginPage.invalidCredentialsDisplayed: "Login is rejected"
                }
            }
        }
    }

    private void withAccount(boolean disabled = false, Closure code) {
        def name = uid('A')
        def password = "xxxx"
        def fullName = "Damien Coraboeuf"
        def account = ontrack.admin.account(name, fullName, "dcoraboeuf@nemerosa.net", password)
        if (disabled) {
            account.disable()
        }
        code(name, password, fullName)
    }

}
