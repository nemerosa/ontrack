package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.pages.HomePage
import net.nemerosa.ontrack.acceptance.pages.ProjectDialog
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.MainSteps.connectAsAdmin
import static net.nemerosa.ontrack.acceptance.steps.MainSteps.withProject
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

    @Test
    void 'Branch creation'() {
        withProject { projectPage ->
            // Login
            projectPage.login 'admin', adminPassword
            // Creates a branch
            def branchName = uid('B')
            projectPage.createBranch {
                name = branchName
                description = "Branch $branchName"
            }
            // Checks the branch is created
            assert projectPage.isBranchPresent(branchName)
        }
    }

}
