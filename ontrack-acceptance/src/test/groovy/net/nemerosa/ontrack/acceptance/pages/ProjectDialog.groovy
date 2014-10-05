package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class ProjectDialog extends AbstractDialog {

    @FindBy(name = 'name')
    WebElement nameInput

    @FindBy(name = 'description')
    WebElement descriptionInput

    ProjectDialog(WebDriver driver) {
        super(driver)
    }

    void setName(String value) {
        nameInput.sendKeys value
    }

    void setDescription(String value) {
        descriptionInput.sendKeys value
    }
}
