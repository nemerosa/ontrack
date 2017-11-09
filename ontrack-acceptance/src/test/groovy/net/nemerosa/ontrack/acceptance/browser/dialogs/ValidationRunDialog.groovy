package net.nemerosa.ontrack.acceptance.browser.dialogs

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.support.AbstractDialog
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy
import org.openqa.selenium.support.ui.Select

class ValidationRunDialog extends AbstractDialog<ValidationRunDialog> {

    @FindBy(id = "validationStampData")
    private WebElement validationStampData

    @FindBy(id = "validationStampData-form")
    private WebElement theValidationStampDataForm

    ValidationRunDialog(Browser browser) {
        super(browser)
    }

    void setValidationStamp(String validationStamp) {
        new Select(validationStampData).selectByVisibleText(validationStamp)
    }

    WebElement getValidationStampDataForm() {
        browser.waitUntil { theValidationStampDataForm.displayed }
        return theValidationStampDataForm
    }
}
