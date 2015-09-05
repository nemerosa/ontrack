package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.BranchDialog
import org.openqa.selenium.By

class BuildPage extends AbstractHeaderPage {

    BuildPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/build/${parameters.id}"
    }

}
