package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.AcceptanceTestContext
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Before
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * UI tests for the home page
 */
@AcceptanceTestSuite
@AcceptanceTest([AcceptanceTestContext.BROWSER_TEST])
class ACCBrowserHomePage extends AcceptanceTestClient {

    @Before
    void 'Clear projects first'() {
        deleteAllProjects()
    }

    @Test
    void 'Display list of projects when less than N'() {
        withNProjects(5) { projects ->
            def lastProject = projects.last()
            withMaxProjects(10) {
                browser { browser ->
                    HomePage home = loginAsAdmin(browser)
                    assert home.isProjectPresent(lastProject.name): "Project ${lastProject.name} is visible"
                    assert !home.isProjectSearchAvailable(): "Project search is not available"
                }
            }
        }
    }

    @Test
    void 'Do not display list of projects when more than N'() {
        withNProjects(5) { projects ->
            def lastProject = projects.last()
            withMaxProjects(2) {
                browser { browser ->
                    HomePage home = loginAsAdmin(browser)
                    assert !home.isProjectPresent(lastProject.name): "Project ${lastProject.name} is not visible"
                    assert home.isProjectSearchAvailable(): "Project search is available"
                }
            }
        }
    }

    @Test
    void 'Look for an existing project when less than N'() {
        withNProjects(5) { projects ->
            def lastProject = projects.last()
            withMaxProjects(10) {
                browser { browser ->
                    HomePage home = loginAsAdmin(browser)
                    assert !home.isSearchProjectPresent() : "Search box for projects is not present"
                    browser.waitUntil {
                        home.isProjectPresent(lastProject.name)
                    }
                    assert !home.isNoSearchResultPresent() : "Warning for no project result is not present"
                }
            }
        }
    }

    @Test
    void 'Look for an existing project when more than N'() {
        withNProjects(5) { projects ->
            def lastProject = projects.last()
            withMaxProjects(2) {
                browser { browser ->
                    HomePage home = loginAsAdmin(browser)
                    home.searchProject(lastProject.name)
                    browser.waitUntil {
                        home.isProjectPresent(lastProject.name)
                    }
                    assert !home.isNoSearchResultPresent() : "Warning for no project result is not present"
                }
            }
        }
    }

    @Test
    void 'Look for a non existing project when more than N'() {
        withNProjects(5) { projects ->
            String x = uid("P")
            withMaxProjects(2) {
                browser { browser ->
                    HomePage home = loginAsAdmin(browser)
                    home.searchProject(x)
                    assert home.isNoSearchResultPresent() : "Warning for no project result is present"
                    assert !home.isProjectPresent(x)
                }
            }
        }
    }
}
