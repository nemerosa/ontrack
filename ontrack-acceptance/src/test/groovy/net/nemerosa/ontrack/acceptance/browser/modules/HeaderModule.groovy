package net.nemerosa.ontrack.acceptance.browser.modules

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.support.AbstractModule

class HeaderModule extends AbstractModule {

    HeaderModule(Browser browser) {
        super(browser)
    }

    String getUserName() {
        $('#header-user-menu').text
    }

}
