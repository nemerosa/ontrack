package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

import static net.nemerosa.ontrack.acceptance.GUITestClient.waitUntil

class HomePage extends AbstractHeaderPage {

    @FindBy(className = 'ot-command-project-new')
    WebElement createProjectCommand

    HomePage(WebDriver driver) {
        super(driver)
    }

    def createProject() {
        waitUntil { createProjectCommand.displayed }
        createProjectCommand.click()
        new ProjectDialog(driver).waitFor() as ProjectDialog
    }

    def isProjectPresent(String name) {
        waitUntil { driver.findElement(By.linkText(name)).displayed }
        true
    }
}
