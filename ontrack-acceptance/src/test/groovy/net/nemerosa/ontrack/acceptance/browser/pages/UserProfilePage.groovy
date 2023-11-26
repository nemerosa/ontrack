package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class UserProfilePage extends AbstractHeaderPage {

    @FindBy(id = "ot-user-profile-generate-token-name")
    protected WebElement tokenGenerateName

    @FindBy(id = "ot-user-profile-generate-token-button")
    protected WebElement tokenGenerateButton

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
        browser.waitUntil("Token generation name") { tokenGenerateName.displayed }
    }

    /**
     * Generates and copies a token
     */
    String generateToken(String name) {
        tokenGenerateName.sendKeys(name)
        tokenGenerateButton.click()
        def tokenValue = browser.findElement(By.xpath("//table[@id='ot-user-profile-token-list']//tr[@id='token-${name}']//input"))
        browser.waitUntil("Token generation") {
            if (tokenValue.displayed) {
                def text = tokenValue.getAttribute("value")
                text != null && text != ""
            } else {
                false
            }
            def text = getTokenValue()
            text != null && text != ""
        }
        return tokenValue.getAttribute("value")
    }
}
