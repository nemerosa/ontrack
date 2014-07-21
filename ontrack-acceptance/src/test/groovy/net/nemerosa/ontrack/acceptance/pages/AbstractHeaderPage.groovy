package net.nemerosa.ontrack.acceptance.pages

import org.openqa.selenium.WebDriver

abstract class AbstractHeaderPage extends AbstractPage {

    final HeaderPageComponent header

    AbstractHeaderPage(WebDriver driver) {
        super(driver)
        header = new HeaderPageComponent(driver)
    }

    def login(String name, String password) {
        header.login(name, password)
        this
    }

}
