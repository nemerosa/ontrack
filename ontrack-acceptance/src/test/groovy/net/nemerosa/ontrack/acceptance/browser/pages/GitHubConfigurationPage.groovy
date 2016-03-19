package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.GitHubConfigurationDialog
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

/**
 * GitHub configuration page.
 */
class GitHubConfigurationPage extends AbstractHeaderPage {

    @FindBy(className = 'ot-command-new')
    protected WebElement configurationCreate;

    GitHubConfigurationPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/extension/github/configurations"
    }

    public GitHubConfigurationDialog createConfiguration(Closure closure) {
        def command = configurationCreate
        command.click()
        GitHubConfigurationDialog dialog = new GitHubConfigurationDialog(browser).waitFor()
        closure.delegate = dialog
        closure(dialog)
        dialog.ok()
        return dialog
    }

    /**
     * Get the configuration for a given name
     */
    Map<String, String> getConfiguration(String name) {
        def line = browser.findElement(By.cssSelector("tr[name='${name}']"))
        return [
                name: line.findElement(By.cssSelector("td:first-child > code")).text,
                url: line.findElement(By.cssSelector("td:nth-child(2) > a")).text,
        ]
    }

    @Override
    void waitFor() {
        browser.waitUntil("Page title") { pageTitle.displayed }
    }
}
