package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.browser.Browser.browser

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

}
