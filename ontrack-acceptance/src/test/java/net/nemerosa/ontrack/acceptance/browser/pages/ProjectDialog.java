package net.nemerosa.ontrack.acceptance.browser.pages;

import net.nemerosa.ontrack.acceptance.browser.Browser;
import net.nemerosa.ontrack.acceptance.browser.support.AbstractDialog;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ProjectDialog extends AbstractDialog<ProjectDialog> {

    @FindBy(name = "name")
    private WebElement nameInput;

    @FindBy(name = "description")
    private WebElement descriptionInput;

    public ProjectDialog(Browser browser) {
        super(browser);
    }

    public void setName(String value) {
        nameInput.sendKeys(value);
    }

    public void setDescription(String value) {
        descriptionInput.sendKeys(value);
    }
}
