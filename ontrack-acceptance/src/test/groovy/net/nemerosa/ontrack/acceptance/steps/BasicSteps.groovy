package net.nemerosa.ontrack.acceptance.steps

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage

import static net.nemerosa.ontrack.acceptance.browser.Configuration.getAdminPassword

class BasicSteps {

    static HomePage loginAsAdmin(Browser browser) {
        browser.with {
            def home = goTo HomePage, [:]
            home.login 'admin', browser.configuration.acceptanceConfig.admin
            waitUntil("User name is 'Administrator'") { home.header.userName == 'Administrator' }
            waitUntil("Project creation button is displayed") { home.menuLoaded }
            home
        }
    }

}
