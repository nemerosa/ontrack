package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

import static net.nemerosa.ontrack.acceptance.GUITestClient.waitUntil

class ProjectPage extends AbstractHeaderPage {

    @FindBy(className = 'ot-command-branch-new')
    WebElement createBranchCommand

    ProjectPage(WebDriver driver) {
        super(driver)
    }

    def createBranch(Closure closure) {
        waitUntil { createBranchCommand.displayed }
        createBranchCommand.click()
        BranchDialog dialog = new BranchDialog(driver).waitFor() as BranchDialog
        closure.delegate = dialog
        closure()
        dialog.ok()
    }

    def isBranchPresent(String name) {
        waitUntil { driver.findElement(By.linkText(name)).displayed }
        true
    }

}
