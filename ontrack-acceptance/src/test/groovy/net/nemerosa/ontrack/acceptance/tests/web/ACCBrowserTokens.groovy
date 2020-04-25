package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.AcceptanceTestContext
import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.login
import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
@AcceptanceTest([AcceptanceTestContext.BROWSER_TEST])
class ACCBrowserTokens extends AcceptanceTestClient {

    @Test
    void 'Generate a token with the UI and use it as password in the UI'() {
        // Creates an account
        def username = uid("U")
        def password = uid("P")
        doCreateAccount(username, password)
        // Login with this account
        browser { Browser browser ->
            def homePage = login(browser, username, password, username)
            // Go to the user profile
            def userProfilePage = homePage.goToUserProfile()
            // Generates and copies the token
            def token = userProfilePage.generateToken()
            // Logout
            def loginPage = userProfilePage.logout()
            // Sign in again
            // ... but with the token
            loginPage.login(username, token)
            homePage.waitFor()
        }
    }

}
