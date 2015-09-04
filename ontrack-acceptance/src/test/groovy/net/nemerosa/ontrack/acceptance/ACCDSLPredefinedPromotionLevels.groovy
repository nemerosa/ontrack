package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the predefined promotion levels
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
class ACCDSLPredefinedPromotionLevels extends AbstractACCDSL {

    @Test
    void 'Auto creation of promotion levels for an authorised project'() {
        // Name of the promotion level
        def plName = uid('PL')
        // Creation of a predefined promotion level with an image
        ontrack.configure {
            predefinedPromotionLevel(plName, 'Promotion level') {
                image getImageFile()
            }
        }
        // Checks it has been created
        def pvs = ontrack.config.predefinedPromotionLevels.find { it.name == plName }
        assert pvs != null
        // Downloading the image
        def image = pvs.image
        assert image.type == 'image/png;charset=UTF-8'
        assert image.content == imageFile.bytes

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // Enabling the auto promotion levels on the project
        ontrack.project(projectName).config {
            autoPromotionLevel()
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing promotion level on the branch
        build.promote(plName)

        // Checks the promotion level has been created
        def vs = ontrack.promotionLevel(projectName, 'B', plName)
        assert vs.id > 0
        image = pvs.image
        assert image.type == 'image/png;charset=UTF-8'
        assert image.content == imageFile.bytes
    }

    @Test
    void 'Auto creation of promotion levels for an authorised project but with a non existing promotion level'() {
        // Name of the promotion level
        def plName = uid('PL')

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // Enabling the auto promotion levels on the project
        ontrack.project(projectName).config {
            autoPromotionLevel()
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing promotion level on the branch
        validationError("Promotion level not found: ${projectName}/B/${plName}") {
            build.promote(plName)
        }
    }

    @Test
    void 'No creation of promotion levels for a non authorised project'() {
        // Name of the promotion level
        def plName = uid('PL')

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // NOT enabling the auto promotion levels on the project
        ontrack.project(projectName).config {
            autoPromotionLevel(false)
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing promotion level on the branch
        validationError("Promotion level not found: ${projectName}/B/${plName}") {
            build.promote(plName)
        }
    }

    @Test
    void 'No creation of promotion levels by default'() {
        // Name of the promotion level
        def plName = uid('PL')

        // Creating a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B')
        }

        // Creates a build
        def build = ontrack.branch(projectName, 'B').build('1')

        // Validates a build using a non existing promotion level on the branch
        validationError("Promotion level not found: ${projectName}/B/${plName}") {
            build.promote(plName)
        }
    }

}
