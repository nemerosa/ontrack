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

    public ProjectDialog createProject(Closure closure) {
        def createProjectCommand = $('.ot-command-project-new')
        browser.waitUntil { createProjectCommand.displayed }
        createProjectCommand.click()
        ProjectDialog dialog = new ProjectDialog(browser).waitFor()
        closure.delegate = dialog
        closure(dialog)
        dialog.ok()
        // Returns the dialog
        return dialog
    }

    public boolean isProjectPresent(String name) {
        assert $(By.linkText(name)).displayed
        true
    }

    ProjectPage goToProject(String name) {
        $(By.linkText(name)).click()
        browser.at(ProjectPage)
    }
}
