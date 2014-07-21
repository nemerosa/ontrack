package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

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
        driver.findElement(By.name('name')).sendKeys name
        driver.findElement(By.name('password')).sendKeys password
        driver.findElement(By.className('btn-primary')).click()
    }
}
