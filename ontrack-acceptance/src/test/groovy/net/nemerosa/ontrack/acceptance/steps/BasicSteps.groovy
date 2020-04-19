package net.nemerosa.ontrack.acceptance.steps

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import net.nemerosa.ontrack.acceptance.browser.pages.LoginPage

class BasicSteps {

    static HomePage loginAsAdmin(Browser browser) {
        browser.with {
            def home = login(browser, 'admin', browser.configuration.acceptanceConfig.admin, 'Administrator')
            waitUntil("Project creation button is displayed") { home.menuLoaded }
            home
        }
    }

    static HomePage login(Browser browser, String name, String password, String fullName) {
        browser.with {
            def loginPage = goTo LoginPage, [:]
            loginPage.login name, password
            def home = at(HomePage)
            waitUntil("User name is '${fullName}'") { home.header.userName == fullName }
            return home
        }
    }

}
