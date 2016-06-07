package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.By

class AccountManagementPage extends AbstractHeaderPage {

    AccountManagementPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/admin-accounts"
    }

    @Override
    void waitFor() {
        browser.waitUntil("Create account") { browser.findElement(By.id("admin-accounts-create")).displayed }
    }

}
