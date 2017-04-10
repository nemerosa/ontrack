package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * GUI tests about the promotions being displayed in the build link decorations
 */
@AcceptanceTestSuite
class ACCBrowserBuildLinkPromotions extends AcceptanceTestClient {

    @Test
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
        build2.buildLink projectName, '1'

        // Goes to the build page
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the build page which must contains the link
            BuildPage buildPage = goTo(BuildPage, [id: build2.id])
            // Get the decoration
            Collection<WebElement> decorations = buildPage.findDecorations('net.nemerosa.ontrack.extension.general.BuildLinkDecorationExtension')
            assert decorations.size() == 1
            WebElement decoration = decorations[0]
            // Gets the link to the build
            assert decoration.findElement(By.linkText("1 @ ${projectName}")) != null
            // Gets the images for the two promotions
            assert decoration.findElement(By.cssSelector("img.ot-icon[title='COPPER']")) != null
            assert decoration.findElement(By.cssSelector("img.ot-icon[title='BRONZE']")) != null
        }
    }

}
