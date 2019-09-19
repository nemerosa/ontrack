package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import org.openqa.selenium.WebElement

class ValidationStampPage extends AbstractHeaderPage {

    ValidationStampPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/validationStamp/${parameters.id}"
    }

    WebElement getBulkUpdateCommand() {
        return $("#bulkUpdateValidationStamp")
    }
}
