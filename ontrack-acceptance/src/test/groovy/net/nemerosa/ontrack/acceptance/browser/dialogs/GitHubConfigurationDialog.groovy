package net.nemerosa.ontrack.acceptance.browser.dialogs

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.support.AbstractDialog
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

public class GitHubConfigurationDialog extends AbstractDialog<GitHubConfigurationDialog> {

    @FindBy(name = "name")
    private WebElement nameInput;

    public GitHubConfigurationDialog(Browser browser) {
        super(browser);
    }

    public void setName(String value) {
        nameInput.sendKeys(value);
    }

}
