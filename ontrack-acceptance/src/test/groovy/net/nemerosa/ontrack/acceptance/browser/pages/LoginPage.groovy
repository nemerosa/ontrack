package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class LoginPage extends AbstractPage {

    @FindBy(id = 'username')
    protected WebElement username

    @FindBy(id = 'password')
    protected WebElement password

    @FindBy(id = 'submit')
    protected WebElement submit

    @FindBy(id = 'error-invalid-credentials')
    private WebElement invalidCredentials

    LoginPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        return "login"
    }

    @Override
    void waitFor() {
        browser.waitUntil("User name") { username.displayed }
    }

    boolean hasExtension(String id) {
        def extension = browser.findElement(By.id(id))
        return extension.displayed
    }

    void useExtension(String id) {
        def extension = browser.findElement(By.id(id))
        extension.click()
    }

    void login(String name, String password) {
        doLogin(name, password)
    }

    void checkOnLogin() {
        assert username.displayed
        assert password.displayed
    }

    void doLogin(String name, String password, long waitMs = 500) {
        this.username.sendKeys(name)

        this.password.sendKeys(password)

        // Sign in OK
        browser.waitUntil { submit.enabled }
        submit.click()

        /**
         * Here, the whole page is now reloaded
         *
         * Waiting a bit
         */
        sleep waitMs
    }

    boolean isInvalidCredentialsDisplayed() {
        return invalidCredentials.displayed
    }

}
