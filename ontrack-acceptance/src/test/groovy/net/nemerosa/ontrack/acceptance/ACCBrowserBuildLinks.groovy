package net.nemerosa.ontrack.acceptance


import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * GUI test which tests the links between builds.
 */
@AcceptanceTestSuite
class ACCBrowserBuildLinks extends AcceptanceTestClient {

    @Test
    void 'Using links navigation'() {

        // Target project
        def targetProjectName = uid("P")
        ontrack.project(targetProjectName) {
            branch("master") {
                // Creates 25 builds
                (1..25).each { no ->
                    build("1.$no")
                }
            }
        }

        // Source build
        def sourceProjectName = uid("P")
        ontrack.project(sourceProjectName) {
            branch("master") {
                def build = build("2.0")
                // Creates all the links
                (1..25).each { no ->
                    build.buildLink(targetProjectName, "1.$no")
                }
            }
        }
        def build = ontrack.build(sourceProjectName, "master", "2.0")

        // Go to the build page

        browser { browser ->
            // Logging in
            loginAsAdmin(browser)
            // Going to the build page
            BuildPage buildPage = goTo(BuildPage, [id: build.id])

            // Checks that we have 10 "Using" links being displayed (from 25 to 16)
            (25..16).each { no ->
                assert buildPage.getUsingBuildLink("1.$no").isDisplayed() : "1.$no link is present"
            }

            // Checks that the Previous button is not accessible
            assert !buildPage.usingPreviousButton.isDisplayed() : "Previous button is not accessible"

            // Checks that the Next button is there, and clicks on it
            assert buildPage.usingNextButton.isDisplayed() : "Next button is accessible"
            buildPage.usingNextButton.click()

            // Checks that we have 10 "Using" links being displayed (from 15 to 6)
            (15..6).each { no ->
                assert buildPage.getUsingBuildLink("1.$no").isDisplayed() : "1.$no link is present"
            }

            // Checks that the Previous button is accessible
            assert buildPage.usingPreviousButton.isDisplayed() : "Previous button is accessible"

            // Checks that the Next button is there, and clicks on it
            assert buildPage.usingNextButton.isDisplayed() : "Next button is accessible"
            buildPage.usingNextButton.click()

            // Checks that we have 5 "Using" links being displayed (from 5 to 1)
            (5..1).each { no ->
                assert buildPage.getUsingBuildLink("1.$no").isDisplayed() : "1.$no link is present"
            }

            // Checks that the Next button is not accessible
            assert !buildPage.usingNextButton.isDisplayed() : "Next button is not accessible"

            // Checks that the Previous button is there, and clicks on it
            assert buildPage.usingPreviousButton.isDisplayed() : "Previous button is accessible"
            buildPage.usingPreviousButton.click()

            // Checks that we have 10 "Using" links being displayed (from 15 to 6)
            (15..6).each { no ->
                assert buildPage.getUsingBuildLink("1.$no").isDisplayed() : "1.$no link is present"
            }
        }

    }

}
