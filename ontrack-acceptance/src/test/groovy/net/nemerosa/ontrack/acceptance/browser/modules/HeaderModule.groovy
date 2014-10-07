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
        $(By.id('header-user-menu')).text
    }

    public void login(String name, String password) {
        $(By.linkText("Sign in")).click()
        browser.screenshot("login-displayed");

        WebElement tName = $(By.name("name"));
        tName.sendKeys(name);

        WebElement tPassword = $(By.name("password"));
        tPassword.sendKeys(password);

        browser.screenshot("login-filled-in");

        // Logging
        trace("Login.name = %s", tName.getAttribute("value"));
        trace("Login.password (size) = %d", tPassword.getAttribute("value").length());

        // Sign in OK
        WebElement okButton = $(By.className("btn-primary"));
        browser.waitUntil { okButton.enabled }
        okButton.click();
    }

}
