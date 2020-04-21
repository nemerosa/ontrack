package net.nemerosa.ontrack.acceptance.tests.web

import net.nemerosa.ontrack.acceptance.AcceptanceTestClient
import net.nemerosa.ontrack.acceptance.browser.dialogs.ValidationRunDialog
import net.nemerosa.ontrack.acceptance.browser.pages.BranchPage
import net.nemerosa.ontrack.acceptance.browser.pages.BuildPage
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.Build
import net.nemerosa.ontrack.dsl.ValidationStamp
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By

import static net.nemerosa.ontrack.acceptance.steps.BasicSteps.loginAsAdmin
import static net.nemerosa.ontrack.test.TestUtils.uid

@AcceptanceTestSuite
class ACCBrowserValidationRunData extends AcceptanceTestClient {

    private Build bld
    private ValidationStamp vs

    @Before
    void prepare() {
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B') {
                validationStamp('Coverage').setPercentageDataType(40, 20, true)
                build '1'
            }
        }
        bld = ontrack.build(projectName, 'B', '1')
        vs = ontrack.validationStamp(projectName, 'B', 'Coverage')
    }

    @Test
    void 'Validation a build with data from the branch overview'() {
        // Gets the branch
        def branch = ontrack.branch(bld.project, bld.branch)

        // GUI scenario
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the branch page
            BranchPage branchPage = goTo(BranchPage, [id: branch.id])
            // Click on the validation
            ValidationRunDialog dialog = branchPage.validate(bld, vs)
            // Selects the "Coverage" timestamp
            dialog.validationStamp = 'Coverage'
            // Enters a value
            dialog.validationStampDataForm.findElement(By.name("value")).sendKeys("25")
            // Validates
            dialog.ok()
        }

        // Checks the validation run
        checkValidationRun()
    }

    @Test
    void 'Validating a build with data from the build page'() {
        // GUI scenario
        browser { browser ->
            // Logs in
            loginAsAdmin(browser)
            // Goes to the build page which must contains the link
            BuildPage buildPage = goTo(BuildPage, [id: bld.id])
            browser.screenshot("build-page")
            // Validation link
            ValidationRunDialog dialog = buildPage.validate()
            browser.screenshot("validation-run-dialog-open")
            // Selects the "Coverage" timestamp
            dialog.validationStamp = 'Coverage'
            browser.screenshot("validation-run-dialog-vs")
            // Enters a value
            dialog.validationStampDataForm.findElement(By.name("value")).sendKeys("25")
            browser.screenshot("validation-run-dialog-data")
            // Validates
            dialog.ok()
            browser.screenshot("validation-run-dialog-ok")
        }

        // Checks the validation run
        checkValidationRun()
    }

    protected void checkValidationRun() {
        def run = bld.validationRuns[0]
        assert run.status == "WARNING"
        assert run.data != null
        assert run.data.id == "net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType"
        assert run.data.data == 25
    }

}
