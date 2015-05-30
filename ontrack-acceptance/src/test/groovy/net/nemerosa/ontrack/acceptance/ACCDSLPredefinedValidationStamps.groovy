package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the predefined validation stamps
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
class ACCDSLPredefinedValidationStamps extends AbstractACCDSL {

    @Test
    void 'Auto creation of validation stamps for an authorised project'() {
        // Name of the validation stamp
        def vsName = uid('VS')
        // Creation of a predefined validation stamp with an image
        ontrack.configure {
            predefinedValidationStamp(vsName, 'Validation stamp') {
                image getImageFile()
            }
        }
        // Checks it has been created
        def vs = ontrack.config.predefinedValidationStamps.find { it.name == vsName }
        assert vs != null
        // Downloading the image
        def image = vs.image
        assert image.type == 'image/png;charset=UTF-8'
        assert image.content == imageFile.bytes

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // Enabling the auto validation stamps on the project
        ontrack.project(projectName).config {
            autoValidationStamp()
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing validation stamp on the branch
        build.validate(vsName)

        // Checks the validation stamp has been created
        assert ontrack.validationStamp(projectName, 'B', vsName).id > 0
    }

}
