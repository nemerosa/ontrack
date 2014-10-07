package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.ProjectDialog
import org.openqa.selenium.By

public class HomePage extends AbstractHeaderPage {

    public HomePage(Browser browser) {
        super(browser);
    }

    @Override
    public String getPath(Map<String, Object> parameters) {
        return "index.html";
    }

    public void createProject(Closure closure) {
        def createProjectCommand = driver.findElement(By.className('ot-command-project-new'))
        browser.waitUntil { createProjectCommand.displayed }
        createProjectCommand.click()
        ProjectDialog dialog = new ProjectDialog(browser).waitFor()
        closure.delegate = dialog
        closure(dialog)
        dialog.ok()
    }

    public boolean isProjectPresent(String name) {
        browser.waitUntil { driver.findElement(By.linkText(name)).displayed }
        true
    }

    ProjectPage goToProject(String name) {
        driver.findElement(By.linkText(name)).click()
        browser.at(ProjectPage)
    }
}
