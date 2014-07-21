package net.nemerosa.ontrack.acceptance

import com.google.common.base.Predicate
import net.nemerosa.ontrack.acceptance.pages.HomePage
import org.junit.Test
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait

class ACCGUISmoke extends GUITestClient {

    @Test
    void 'Home page'() {
        HomePage home = startApplication()
        assert home.header.signIn.text == 'Sign in'
    }

    @Test
    void 'Admin login'() {
        HomePage home = startApplication()
        home.login('admin', getAdminPassword())
        new WebDriverWait(driver, 10).until(new Predicate<WebDriver>() {
            @Override
            boolean apply(WebDriver input) {
                home.header.userMenu.text == 'Administrator'
            }
        })
    }

}
