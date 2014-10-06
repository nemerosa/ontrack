package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.browser.Browser.browser
import static net.nemerosa.ontrack.acceptance.browser.Configuration.getAdminPassword
import static net.nemerosa.ontrack.test.TestUtils.uid

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

    @Test
    void 'Project creation'() {
        browser {
            HomePage home = goTo HomePage, [:]
            home.login 'admin', adminPassword

            def projectName = uid('P')
            home.createProject {
                name = projectName
                description = "Project ${projectName}"
            }

            // Checks the project is visible in the list
            assert home.isProjectPresent(projectName)
        }
    }

}
