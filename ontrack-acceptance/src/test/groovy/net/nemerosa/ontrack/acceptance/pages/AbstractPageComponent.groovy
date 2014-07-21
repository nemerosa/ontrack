package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.PageFactory

abstract class AbstractPageComponent {

    final WebDriver driver

    AbstractPageComponent(WebDriver driver) {
        this.driver = driver
        PageFactory.initElements((WebDriver) driver, this)
    }
}
