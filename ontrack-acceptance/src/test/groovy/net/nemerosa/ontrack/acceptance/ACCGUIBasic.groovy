package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import org.junit.Test

/**
 * Basic GUI tests
 */
class ACCGUIBasic {

    @Test
    void 'Home page is accessible'() {
        Browser.browser({ browser ->
            browser.goTo HomePage, [:]
        })
    }

}
