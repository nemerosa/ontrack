package net.nemerosa.ontrack.acceptance.browser.support;

import net.nemerosa.ontrack.acceptance.browser.Browser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HeaderModule extends AbstractModule {

    @FindBy(linkText = "Sign in")
    private WebElement signIn;

    @FindBy(id = "header-user-menu")
    private WebElement userMenu;

    public HeaderModule(Browser browser) {
        super(browser);
    }

    public void login(String name, String password) {
        signIn.click();
        browser.screenshot("login-displayed");

        WebElement tName = driver.findElement(By.name("name"));
        tName.sendKeys(name);

        WebElement tPassword = driver.findElement(By.name("password"));
        tPassword.sendKeys(password);

        browser.screenshot("login-filled-in");

        // Logging
        trace("Login.name = %s", tName.getAttribute("value"));
        trace("Login.password (size) = %d", tPassword.getAttribute("value").length());

        // Sign in OK
        WebElement okButton = driver.findElement(By.className("btn-primary"));
        browser.waitUntil(okButton::isEnabled);
        okButton.click();
    }

}
