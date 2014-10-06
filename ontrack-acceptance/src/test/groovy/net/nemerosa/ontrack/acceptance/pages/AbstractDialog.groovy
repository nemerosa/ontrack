package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

import static net.nemerosa.ontrack.acceptance.GUITestClient.waitUntil

abstract class AbstractDialog extends AbstractPageComponent {

    @FindBy(className = 'btn-primary')
    protected WebElement okButton

    AbstractDialog(WebDriver driver) {
        super(driver)
    }

    AbstractDialog waitFor() {
        waitUntil { okButton.displayed }
        this
    }

    def ok() {
        assert okButton.enabled
        okButton.click()
    }
}
