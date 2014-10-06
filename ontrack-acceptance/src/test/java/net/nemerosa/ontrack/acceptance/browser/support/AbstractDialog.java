package net.nemerosa.ontrack.acceptance.browser.support;

import net.nemerosa.ontrack.acceptance.browser.Browser;
import org.apache.commons.lang3.Validate;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AbstractDialog<D extends AbstractDialog<D>> extends AbstractModule {

    @FindBy(className = "btn-primary")
    protected WebElement okButton;

    protected AbstractDialog(Browser browser) {
        super(browser);
    }

    public D waitFor() {
        browser.waitUntil(okButton::isDisplayed);
        //noinspection unchecked
        return (D) this;
    }

    public void ok() {
        Validate.isTrue(okButton.isEnabled());
        okButton.click();
    }

}
