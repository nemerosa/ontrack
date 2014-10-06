package net.nemerosa.ontrack.acceptance.browser.pages;

import net.nemerosa.ontrack.acceptance.browser.Browser;
import net.nemerosa.ontrack.acceptance.browser.support.HeaderModule;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public abstract class AbstractHeaderPage extends AbstractPage {

    @FindBy(className = "ot-view-title")
    protected WebElement pageTitle;

    private final HeaderModule header;

    public AbstractHeaderPage(Browser browser) {
        super(browser);
        header = new HeaderModule(browser);
    }

    @Override
    public void waitFor() {
        browser.waitUntil(pageTitle::isDisplayed);
    }

    public void login(String user, String password) {
        header.login(user, password);
    }

}
