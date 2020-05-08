package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class KeycloakLoginPage extends AbstractPage {

    @FindBy(id = 'username')
    protected WebElement username

    @FindBy(id = 'password')
    protected WebElement password

    @FindBy(id = 'kc-login')
    protected WebElement loginButton

    KeycloakLoginPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        return "login.html"
    }

    @Override
    void waitFor() {
        browser.waitUntil("KC Login") { loginButton.displayed }
    }

    HomePage login(String name, String password) {
        this.username.sendKeys(name)
        this.password.sendKeys(password)
        this.loginButton.click()
        return browser.at(HomePage)
    }
}
