package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.pages.HomePage
import net.nemerosa.ontrack.acceptance.pages.ProjectDialog
import net.nemerosa.ontrack.acceptance.steps.MainSteps
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.MainSteps.connectAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

class ACCGUISmoke extends GUITestClient {

    @Test
    void 'Home page'() {
        HomePage home = startApplication()
        assert home.header.signIn.text == 'Sign in'
    }

    @Test
    void 'Admin login'() {
        HomePage home = connectAsAdmin()
        waitUntil {
            home.header.userMenu.text == 'Administrator'
        }
    }

    @Test
    void 'Project creation'() {
        // Starts and logs
        HomePage home = connectAsAdmin()
        // Creates a project
        ProjectDialog dialog = home.createProject()
        def projectName = uid('P')
        try {
            dialog.name = projectName
            dialog.description = "Project ${projectName}"
            dialog.ok()
            // Checks the project is visible in the list
            assert home.isProjectPresent(projectName)
        } finally {
            doDeleteProject projectName
        }
    }

}
