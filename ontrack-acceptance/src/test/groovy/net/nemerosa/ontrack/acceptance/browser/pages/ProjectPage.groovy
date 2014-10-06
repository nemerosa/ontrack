package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.BranchDialog
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class ProjectPage extends AbstractHeaderPage {

    @FindBy(className = 'ot-command-branch-new')
    private WebElement createBranchCommand

    ProjectPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/project/${parameters.id}"
    }

    void createBranch(Closure closure) {
        browser.waitUntil { createBranchCommand.displayed }
        createBranchCommand.click()
        BranchDialog dialog = new BranchDialog(browser).waitFor()
        closure.delegate = dialog
        closure(dialog)
        dialog.ok()
    }

    boolean isBranchPresent(String branchName) {
        browser.waitUntil { driver.findElement(By.linkText(branchName)).displayed }
        true
    }
}
