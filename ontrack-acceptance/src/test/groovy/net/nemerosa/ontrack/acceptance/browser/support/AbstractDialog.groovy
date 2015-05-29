package net.nemerosa.ontrack.acceptance.browser.support

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

public class AbstractDialog<D extends AbstractDialog<D>> extends AbstractModule {

    @FindBy(className = "ot-dialog-ok")
    protected WebElement okButton

    @FindBy(className = "ot-dialog-cancel")
    protected WebElement cancelButton

    @FindBy(className = "ot-alert-error")
    protected WebElement errorMessage

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

    void cancel() {
        assert cancelButton.enabled
        cancelButton.click()
    }

    boolean isDisplayed() {
        return okButton.displayed
    }

    String getErrorMessage() {
        if (errorMessage.displayed) {
            return errorMessage.text
        } else {
            return ''
        }
    }

}
