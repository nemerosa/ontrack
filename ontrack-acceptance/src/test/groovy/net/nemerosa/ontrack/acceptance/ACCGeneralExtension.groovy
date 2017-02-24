package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebElement

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * GUI tests about the `general` extension.
 */
@AcceptanceTestSuite
class ACCGeneralExtension extends AcceptanceTestClient {

    @Test
    void 'Release decoration'() {

        // Preparation
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B') {
                build '1'
            }
        }

        // Label property on a build
        def build = ontrack.build(projectName, 'B', '1')
        build.config.label 'RC.1'

        // Asserts the decoration is there
        assert build.releaseDecoration == 'RC.1'

        // Goes to the build page
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the build page which must contains the link
            BuildPage buildPage = goTo(BuildPage, [id: build.id])
            // Get the decoration
            Collection<WebElement> decorations = buildPage.findDecorations('net.nemerosa.ontrack.extension.general.ReleaseDecorationExtension')
            assert decorations.size() == 1
            WebElement decoration = decorations[0]
            // Gets the text of the decoration itself
            assert decoration.findElement(By.cssSelector("span.ot-decoration-border")).text.trim() == 'RC.1'
        }
    }

}
