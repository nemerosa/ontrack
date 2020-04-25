package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class UserProfilePage extends AbstractHeaderPage {

    @FindBy(id = 'token')
    protected WebElement token

    @FindBy(id = 'token-generate')
    protected WebElement tokenGenerate

    UserProfilePage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/user-profile"
    }

    @Override
    void waitFor() {
        super.waitFor()
        browser.waitUntil("Page title") { token.displayed }
    }

    /**
     * Gets the token value
     */
    String getTokenValue() {
        return token.getAttribute("value")
    }

    /**
     * Generates and copies a token
     */
    String generateToken() {
        tokenGenerate.click()
        browser.waitUntil("Token generation") {
            def text = getTokenValue()
            text != null && text != ""
        }
        return tokenValue
    }
}
