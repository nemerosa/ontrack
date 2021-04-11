package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.ProjectDialog
import org.mockito.internal.matchers.Find
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

import javax.xml.ws.WebEndpoint

public class HomePage extends AbstractHeaderPage {

    @FindBy(className = 'ot-command-api')
    protected WebElement commandApi

    public HomePage(Browser browser) {
        super(browser);
    }

    @Override
    void waitFor() {
        super.waitFor()
        browser.waitUntil("API command") { commandApi.displayed }
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

    boolean isProjectPresent(String name) {
        return isElementDisplayed(By.linkText(name))
    }

    boolean isProjectSearchAvailable() {
        return isElementDisplayed(By.id("name-pattern"))
    }

    ProjectPage goToProject(String name) {
        $(By.linkText(name)).click()
        browser.at(ProjectPage)
    }

    boolean isMenuLoaded() {
        $('.ot-command-project-new').displayed
    }

    void searchProject(String name) {
        $('#name-pattern').sendKeys(name)
        $('#search-projects').click()
    }

    boolean isNoSearchResultPresent() {
        return isElementDisplayed(By.id("no-search-result"))
    }

    boolean isSearchProjectPresent() {
        return isElementDisplayed(By.id("name-pattern"))
    }
}
