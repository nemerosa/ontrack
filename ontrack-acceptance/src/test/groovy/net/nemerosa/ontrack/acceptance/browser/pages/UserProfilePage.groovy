package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class UserProfilePage extends AbstractHeaderPage {

    @FindBy(id = 'token')
    protected WebElement token

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
        browser.waitUntil("Token generation button") { browser.findElement(By.id("token-generate")).displayed }
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
        def tokenGenerate = browser.findElement(By.id("token-generate"))
        tokenGenerate.click()
        browser.waitUntil("Token generation") {
            def text = getTokenValue()
            text != null && text != ""
        }
        return tokenValue
    }
}
