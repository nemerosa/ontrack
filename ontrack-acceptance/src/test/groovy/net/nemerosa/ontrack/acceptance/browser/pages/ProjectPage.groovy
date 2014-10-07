package net.nemerosa.ontrack.acceptance.browser.pages

import net.nemerosa.ontrack.acceptance.browser.Browser
import net.nemerosa.ontrack.acceptance.browser.dialogs.BranchDialog
import org.openqa.selenium.By

class ProjectPage extends AbstractHeaderPage {

    ProjectPage(Browser browser) {
        super(browser)
    }

    @Override
    String getPath(Map<String, Object> parameters) {
        "index.html#/project/${parameters.id}"
    }

    void createBranch(Closure closure) {
        def createBranchCommand = $('.ot-command-branch-new')
        browser.waitUntil { createBranchCommand.displayed }
        createBranchCommand.click()
        BranchDialog dialog = new BranchDialog(browser).waitFor()
        closure.delegate = dialog
        closure(dialog)
        dialog.ok()
    }

    boolean isBranchPresent(String branchName) {
        browser.waitUntil { $(By.linkText(branchName)).displayed }
        true
    }
}
