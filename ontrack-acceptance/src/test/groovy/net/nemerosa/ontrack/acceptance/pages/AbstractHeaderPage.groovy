package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver

abstract class AbstractHeaderPage extends AbstractPage {

    HeaderPageComponent header = new HeaderPageComponent()

    AbstractHeaderPage(WebDriver driver) {
        super(driver)
    }
}
