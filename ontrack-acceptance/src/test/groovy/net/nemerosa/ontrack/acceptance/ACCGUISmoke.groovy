package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.pages.HomePage
import org.junit.Test

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
        waitUntil {
            home.header.userMenu.text == 'Administrator'
        }
    }

}
