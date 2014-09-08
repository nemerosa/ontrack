package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.PageFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class AbstractPageComponent {

    final Logger logger = LoggerFactory.getLogger(getClass())
    final WebDriver driver

    AbstractPageComponent(WebDriver driver) {
        this.driver = driver
        PageFactory.initElements((WebDriver) driver, this)
    }

    def trace(String message) {
        logger.debug "[gui] $message"
    }
}
