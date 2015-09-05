package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.APIPage
import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.browser.pages.HomePage
import net.nemerosa.ontrack.acceptance.browser.pages.ProjectPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.acceptance.browser.Browser.browser
import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * GUI tests about the promotions being displayed in the build link decorations
 */
@AcceptanceTestSuite
class ACCBrowserBuildLinkPromotions extends AcceptanceTestClient {

    @Test
    @AcceptanceTest(excludes = "production")
    void 'Promotions are displayed in the build link decorations'() {

        // Preparation
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B') {
                promotionLevel 'COPPER'
                promotionLevel 'BRONZE'
                build '1'
                build '2'
            }
        }

        // Promoting one build
        def build1 = ontrack.build(projectName, 'B', '1')
        build1.promote 'COPPER'
        build1.promote 'BRONZE'

        // Link from another build
        def build2 = ontrack.build(projectName, 'B', '2')
        build2.config.buildLink projectName, '1'

        // Goes to the build page
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the build page which must contains the link
            goTo BuildPage, [id: build2.id]
        }
    }

}
