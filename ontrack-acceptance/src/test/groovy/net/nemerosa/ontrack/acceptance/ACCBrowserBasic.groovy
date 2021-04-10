package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.APIPage
import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import net.nemerosa.ontrack.acceptance.browser.pages.ProjectPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.login
import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid
import static org.junit.Assert.assertEquals

/**
 * Basic GUI tests
 */
@AcceptanceTestSuite
@AcceptanceTest([AcceptanceTestContext.PRODUCTION, AcceptanceTestContext.SMOKE, AcceptanceTestContext.BROWSER_TEST])
class ACCBrowserBasic extends AcceptanceTestClient {

    @Before
    void 'Clear projects first'() {
        deleteAllProjects()
    }

    @Test
    @AcceptanceTest([AcceptanceTestContext.PRODUCTION, AcceptanceTestContext.SMOKE, AcceptanceTestContext.BROWSER_TEST])
    void 'Home page is accessible'() {
        browser {
            goTo HomePage, [:]
        }
    }

    @Test
    @AcceptanceTest(AcceptanceTestContext.SMOKE)
    void 'Admin login'() {
        browser { browser -> loginAsAdmin(browser) }
    }

    @Test
    void 'Account with special characters in password'() {
        def name = uid('A')
        doCreateAccount(name, "test§")
        // Login with this account
        browser { browser ->
            login(browser, name, "test§", name)
        }
    }

    @Test
    @AcceptanceTest(AcceptanceTestContext.SMOKE)
    void 'Project creation'() {
        browser { browser ->
            HomePage home = loginAsAdmin(browser)

            def projectName = uid('P')
            home.createProject {
                name = projectName
                description = "Project ${projectName}"
            }

            // Checks the project is visible in the list
            assert home.isProjectPresent(projectName)
        }
    }

    @Test
    void 'Project creation - name already exists'() {
        def projectName = doCreateProject().path('name').asText()
        browser { browser ->
            HomePage home = loginAsAdmin(browser)

            // Checks the project is visible in the list
            assert home.isProjectPresent(projectName)

            // Tries to create a project with the same name
            def dialog = home.createProject {
                name = projectName
                description = "Project ${projectName}"
            }

            // Checks that there is an error message
            assert dialog.displayed && dialog.errorMessage == "Project name already exists: ${projectName}"

            // Closes the dialog
            dialog.cancel()
        }
    }

    @Test
    void 'Branch creation'() {
        browser { browser ->
            withProject { id, name ->
                // Goes to the home page and logs in browser ->
                HomePage home = loginAsAdmin(browser)
                // Goes to the project
                ProjectPage projectPage = home.goToProject(name)
                // Creates a branch
                def branchName = uid('B')
                projectPage.createBranch { dialog ->
                    dialog.name = branchName
                    dialog.description = "Branch $branchName"
                }
                // Checks the branch is created
                assert projectPage.isBranchPresent(branchName)
            }
        }
    }

    @Test
    void 'Branch creation with a 120 characters long name'() {
        browser { browser ->
            withProject { id, name ->
                // Goes to the home page and logs in browser ->
                HomePage home = loginAsAdmin(browser)
                // Goes to the project
                ProjectPage projectPage = home.goToProject(name)
                // Creates a branch
                def branchName = 'b' * 120
                projectPage.createBranch { dialog ->
                    dialog.name = branchName
                    dialog.description = "Branch $branchName"
                }
                // Checks the branch is created
                assert projectPage.isBranchPresent(branchName)
            }
        }
    }

    @Test
    void 'Project API page must be accessible'() {
        browser { browser ->
            withProject { id, name ->
                // Goes to the home page and logs in browser ->
                HomePage home = loginAsAdmin(browser)
                // Goes to the project
                ProjectPage projectPage = home.goToProject(name)
                // Goes to the API page
                APIPage apiPage = projectPage.goToAPI()
                // Gets the link of the page
                def link = apiPage.apiLink
                // Checks the link
                assert link == "/structure/projects/${id}"
            }
        }
    }

    @Test
    void 'Build page'() {
        def projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                def build = build("1")

                browser { browser ->
                    // Logs in
                    loginAsAdmin(browser)
                    // Goes to the build page which must contains the link
                    BuildPage buildPage = goTo(BuildPage, [id: build.id])
                    // Checks the title
                    assertEquals(
                            "Build 1",
                            buildPage.viewTitle
                    )
                }
            }
        }
    }

}
