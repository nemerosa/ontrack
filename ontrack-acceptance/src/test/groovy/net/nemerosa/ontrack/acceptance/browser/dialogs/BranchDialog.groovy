package net.nemerosa.ontrack.acceptance.browser.dialogs

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.support.AbstractDialog
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class BranchDialog extends AbstractDialog<BranchDialog> {

    @FindBy(name = "name")
    private WebElement nameInput;

    @FindBy(name = "description")
    private WebElement descriptionInput;

    BranchDialog(Browser browser) {
        super(browser);
    }

    void setName(String value) {
        nameInput.sendKeys(value);
    }

    void setDescription(String value) {
        descriptionInput.sendKeys(value);
    }
}
