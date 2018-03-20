package net.nemerosa.ontrack.acceptance.browser.modules

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.support.AbstractModule
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

public class HeaderModule extends AbstractModule {

    public HeaderModule(Browser browser) {
        super(browser);
    }

    public String getUserName() {
        $('#header-user-menu').text
    }

    public void login(String name, String password) {
        $(By.linkText("Sign in")).click()
        doLogin(name, password)
    }

    public void checkOnLogin() {
        assert $(By.name("name")).displayed
        assert $(By.name("password")).displayed
    }

    public void doLogin(String name, String password, long waitMs = 500) {
        WebElement tName = $(By.name("name"));
        tName.sendKeys(name);

        WebElement tPassword = $(By.name("password"));
        tPassword.sendKeys(password);

        // Sign in OK
        WebElement okButton = $(".btn-primary");
        browser.waitUntil { okButton.enabled }
        okButton.click();

        /**
         * Here, the whole page is now reloaded
         *
         * Waiting a bit
         */
        sleep waitMs
    }

}
