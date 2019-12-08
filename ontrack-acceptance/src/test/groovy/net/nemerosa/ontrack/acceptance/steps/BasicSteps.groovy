package net.nemerosa.ontrack.acceptance.steps

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage

class BasicSteps {

    static HomePage loginAsAdmin(Browser browser) {
        browser.with {
            def home = login(browser, 'admin', browser.configuration.acceptanceConfig.admin, 'Administrator')
            waitUntil("Project creation button is displayed") { home.menuLoaded }
            goTo HomePage, [:]
            home
        }
    }

    static HomePage login(Browser browser, String name, String password, String fullName) {
        browser.with {
            def home = goTo HomePage, [:]
            home.login name, password
            waitUntil("User name is '${fullName}'") { home.header.userName == fullName }
            goTo HomePage, [:]
            home
        }
    }

}
