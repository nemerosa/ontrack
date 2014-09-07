package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

import static net.nemerosa.ontrack.acceptance.GUITestClient.screenshot
import static net.nemerosa.ontrack.acceptance.GUITestClient.waitUntil

class HeaderPageComponent extends AbstractPageComponent {

    @FindBy(linkText = 'Sign in')
    WebElement signIn

    @FindBy(id = 'header-user-menu')
    WebElement userMenu

    HeaderPageComponent(WebDriver driver) {
        super(driver)
    }

    def login(String name, String password) {
        signIn.click()
        screenshot('login-displayed')
        driver.findElement(By.name('name')).sendKeys name
        driver.findElement(By.name('password')).sendKeys password
        screenshot('login-filled-in')
        // Sign in OK
        def okButton = driver.findElement(By.className('btn-primary'))
        waitUntil { okButton.enabled }
        okButton.click()
    }
}
