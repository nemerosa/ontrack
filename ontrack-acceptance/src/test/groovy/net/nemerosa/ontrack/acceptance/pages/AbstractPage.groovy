package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver

abstract class AbstractPage extends AbstractPageComponent {

    AbstractPage(WebDriver driver) {
        super(driver)
    }
}
