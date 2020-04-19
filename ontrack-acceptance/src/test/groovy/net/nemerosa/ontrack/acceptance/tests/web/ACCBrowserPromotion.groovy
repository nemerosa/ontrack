package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.AcceptanceTestContext
import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Basic GUI tests
 */
@AcceptanceTestSuite
@AcceptanceTest([AcceptanceTestContext.BROWSER_TEST])
class ACCBrowserPromotion extends AcceptanceTestClient {

    @Test
    void 'Promoting a build'() {
        String projectName = uid("P")
        def buildId
        ontrack.project(projectName) {
            branch("master") {
                promotionLevel("PLATINUM")
                buildId = build("1").id
            }
        }
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the build page which must contains the link
            BuildPage buildPage = goTo(BuildPage, [id: buildId])
            // Opens the promotion dialog
            def promotionRunDialog = buildPage.promote()
            // Selects the promotion
            promotionRunDialog.promotion = "PLATINUM"
            // Validates
            promotionRunDialog.ok()
        }

        // Checks that the build is actually promoted
        def actualBuild = ontrack.build(projectName, "master", "1")
        def run = actualBuild.promotionRuns.find {
            it.promotionLevel.name == "PLATINUM"
        }
        assert run != null: "Build is promoted"
    }

}
