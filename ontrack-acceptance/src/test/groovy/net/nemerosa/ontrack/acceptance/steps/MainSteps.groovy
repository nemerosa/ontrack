package net.nemerosa.ontrack.acceptance.steps

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.pages.HomePage
import net.nemerosa.ontrack.acceptance.pages.ProjectPage

import static net.nemerosa.ontrack.acceptance.GUITestClient.*

class MainSteps {

    private static AcceptanceTestClient testClient = new AcceptanceTestClient()

    static def connectAsAdmin() {
        connect('admin', adminPassword)
    }

    static def connect(String user, String password) {
        HomePage home = startApplication()
        home.login(user, password)
        home
    }

    static <T> T withProject(Closure<T> closure) {
        // Creates a project
        def project = testClient.doCreateProject()
        try {
            // Go to the project's page
            ProjectPage projectPage = goToProject(project.id.asInt())
            // Runs the closure for the project's page
            closure(projectPage)
        } finally {
            // Makes sure to delete the project at the end
            testClient.doDeleteProject project.name.asText()
        }
    }

}
