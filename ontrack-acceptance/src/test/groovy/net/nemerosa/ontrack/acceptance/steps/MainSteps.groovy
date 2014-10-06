package net.nemerosa.ontrack.acceptance.steps

import net.nemerosa.ontrack.acceptance.pages.HomePage

import static net.nemerosa.ontrack.acceptance.GUITestClient.getAdminPassword
import static net.nemerosa.ontrack.acceptance.GUITestClient.startApplication

class MainSteps {

    static def connectAsAdmin() {
        connect('admin', adminPassword)
    }

    static def connect(String user, String password) {
        HomePage home = startApplication()
        home.login(user, password)
        home
    }

}
