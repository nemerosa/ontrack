package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.ValidationRunDialog
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.ValidationStamp
import org.openqa.selenium.WebElement

class BranchPage extends AbstractHeaderPage {

    BranchPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/branch/${parameters.id}"
    }

    ValidationRunDialog validate(Build build, ValidationStamp validationStamp) {
        def button = $("#validation-${build.id}-${validationStamp.id}-validate") as WebElement
        browser.waitUntil { button.displayed }
        button.click()
        return new ValidationRunDialog(browser).waitFor()
    }
}
