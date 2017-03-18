package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.AccountManagementPage
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

/**
 * GUI test which tests the login redirection in case of page not authorised
 */
@AcceptanceTestSuite
class ACCBrowserLoginOn403 extends AcceptanceTestClient {


    @Test
    void 'Login redirection'() {

        browser { browser ->
            // Tries to go to unauthorised page
            browser.goTo AccountManagementPage, [:], false
            // This should be rejected - and we should be on the login page
            browser.screenshot 'access-rejected'
            HomePage home = browser.page(HomePage)
            home.header.checkOnLogin()
            // Now, we login as admin
            home.header.doLogin('admin', adminPassword, 3000)
            // And we should be redirected to the account management page
            browser.screenshot 'access-granted'
            browser.at AccountManagementPage
        }

    }

}
