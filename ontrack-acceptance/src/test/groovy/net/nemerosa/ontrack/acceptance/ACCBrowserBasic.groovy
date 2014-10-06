package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.browser.Browser.browser
import static net.nemerosa.ontrack.acceptance.browser.Configuration.getAdminPassword

/**
 * Basic GUI tests
 */
class ACCBrowserBasic {

    @Test
    void 'Home page is accessible'() {
        browser {
            goTo HomePage, [:]
        }
    }

    @Test
    void 'Admin login'() {
        browser {
            def home = goTo HomePage, [:]
            home.login 'admin', adminPassword
            waitUntil("User name is 'Administrator'") { home.header.userName == 'Administrator' }
        }
    }

}
