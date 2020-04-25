package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.AcceptanceTestContext
import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.pages.LoginPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import java.util.concurrent.TimeUnit

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

    @Test
    void 'Generate a token with the UI and use it in the API'() {
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
            // Uses the token in the API
            def client = ontrackBuilder.authenticate(token).build()
            // Fetches projects for examples
            def projects = client.projects
            assert projects != null: "Projects were fetched using the token"
        }
    }

    @Test
    void 'Trying the connect with an invalid token'() {
        // Creates an account
        def username = uid("U")
        def password = uid("P")
        def accountId = doCreateAccount(username, password)
        // Generates a token for this account, for one day
        def token = ontrack.tokens.generateForAccount(accountId, 1, TimeUnit.DAYS)
        assert token.validUntil != null
        // Connects with this token
        browser { Browser browser ->
            def homePage = login(browser, username, token.value, username)
            homePage.waitFor()
            homePage.logout()
        }
        // Regenerates the token, for one second of validity only
        token = ontrack.tokens.generateForAccount(accountId, 1, TimeUnit.SECONDS)
        // Waits for its expiration
        sleep(2000)
        // Connects with this token
        browser { Browser browser ->
            def loginPage = goTo(LoginPage, [:])
            loginPage.login(username, token.value)
            assert loginPage.invalidCredentialsDisplayed : "Invalid credentials message is displayed."
        }
    }

}
