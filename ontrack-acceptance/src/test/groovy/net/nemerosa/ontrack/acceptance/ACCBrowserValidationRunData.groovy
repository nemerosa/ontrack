package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.browser.dialogs.ValidationRunDialog
import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.ValidationRun
import org.junit.Test
import org.openqa.selenium.By

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCBrowserValidationRunData extends AcceptanceTestClient {

    @Test
    void 'Validating a build with data from the build page'() {
        // Preparation
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B') {
                validationStamp('Coverage').setPercentageDataType(40, 20, true)
                build '1'
            }
        }
        def build1 = ontrack.build(projectName, 'B', '1')

        // GUI scenario
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the build page which must contains the link
            BuildPage buildPage = goTo(BuildPage, [id: build1.id])
            // Validation link
            ValidationRunDialog dialog = buildPage.validate()
            // Selects the "Coverage" timestamp
            dialog.validationStamp = 'Coverage'
            // Enters a value
            dialog.validationStampDataForm.findElement(By.name("value")).sendKeys("25")
            // Validates
            dialog.ok()
        }

        // Checks the validation run
        def run = build1.validationRuns[0]
        assert run.status == "WARNING"
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType"
        assert run.data.data == 25
    }

}
