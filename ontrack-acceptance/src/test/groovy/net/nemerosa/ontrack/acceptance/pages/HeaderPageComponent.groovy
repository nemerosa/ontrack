package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.FindBy

class HeaderPageComponent extends AbstractPageComponent {

    @FindBy(linkText = 'Sign in')
    WebElement signIn

    HeaderPageComponent(WebDriver driver) {
        super(driver)
    }

}
