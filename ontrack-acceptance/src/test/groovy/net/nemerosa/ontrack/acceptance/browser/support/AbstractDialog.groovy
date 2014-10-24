package net.nemerosa.ontrack.acceptance.browser.support

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

public class AbstractDialog<D extends AbstractDialog<D>> extends AbstractModule {

    @FindBy(className = "btn-primary")
    protected WebElement okButton;

    protected AbstractDialog(Browser browser) {
        super(browser);
    }

    public D waitFor() {
        browser.waitUntil { okButton.displayed }
        //noinspection unchecked
        return (D) this;
    }

    public void ok() {
        assert okButton.enabled
        okButton.click()
    }

}
