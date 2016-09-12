package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the predefined validation stamps
 */
@AcceptanceTestSuite
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
        def pvs = ontrack.config.predefinedValidationStamps.find { it.name == vsName }
        assert pvs != null
        // Downloading the image
        def image = pvs.image
        assert image.type == 'image/png'
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
        def vs = ontrack.validationStamp(projectName, 'B', vsName)
        assert vs.id > 0
        image = pvs.image
        assert image.type == 'image/png'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Auto creation of validation stamps for an authorised project but with a non existing validation stamp'() {
        // Name of the validation stamp
        def vsName = uid('VS')

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
        validationError("Validation stamp not found: ${projectName}/B/${vsName}") {
            build.validate(vsName)
        }
    }

    @Test
    void 'No creation of validation stamps for a non authorised project'() {
        // Name of the validation stamp
        def vsName = uid('VS')

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // NOT enabling the auto validation stamps on the project
        ontrack.project(projectName).config {
            autoValidationStamp(false)
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing validation stamp on the branch
        validationError("Validation stamp not found: ${projectName}/B/${vsName}") {
            build.validate(vsName)
        }
    }

    @Test
    void 'No creation of validation stamps by default'() {
        // Name of the validation stamp
        def vsName = uid('VS')

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing validation stamp on the branch
        validationError("Validation stamp not found: ${projectName}/B/${vsName}") {
            build.validate(vsName)
        }
    }

    @Test
    void 'Creation of validation stamps authorised for some projects'() {
        // Name of the validation stamp
        def vsName = uid('VS')

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // Enabling the auto validation stamps on the project, even when not predefiend
        ontrack.project(projectName).config {
            autoValidationStamp(true, true)
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing validation stamp on the branch
        build.validate(vsName)

        // Checks the validation stamp has been created
        def vs = ontrack.validationStamp(projectName, 'B', vsName)
        assert vs.id > 0
        assert vs.name == vsName
        assert vs.description == "Validation automatically created on demand."
    }

}
