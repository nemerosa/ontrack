package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.ProjectDialog
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

public class HomePage extends AbstractHeaderPage {

    @FindBy(className = "ot-command-project-new")
    private WebElement createProjectCommand;

    public HomePage(Browser browser) {
        super(browser);
    }

    @Override
    public String getPath(Map<String, Object> parameters) {
        return "index.html";
    }

    public void createProject(Closure closure) {
        browser.waitUntil { createProjectCommand.displayed }
        createProjectCommand.click()
        ProjectDialog dialog = new ProjectDialog(browser).waitFor()
        closure.delegate = dialog
        closure.apply(dialog)
        dialog.ok()
    }

    public boolean isProjectPresent(String name) {
        browser.waitUntil { driver.findElement(By.linkText(name)).displayed }
    }

}
