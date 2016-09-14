package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the predefined promotion levels
 */
@AcceptanceTestSuite
class ACCDSLPredefinedPromotionLevels extends AbstractACCDSL {

    @Test
    void 'Auto creation of promotion levels must preserve the order'() {
        // Names of the promotion level
        def plNames = (1..4).collect { uid("PL${it}") }
        // Creation of predefined promotion levels
        ontrack.configure {
            plNames.each { plName ->
                predefinedPromotionLevel(plName, "Promotion level ${plName}")
            }
        }

        // Checks the order
        assert ontrack.config.predefinedPromotionLevels
                .collect { it.name }
                .findAll { it in plNames } == plNames

        // Creating a build
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B') {
                build('1')
            }
        }
        def branch = ontrack.branch(projectName, 'B')
        def build = ontrack.build(projectName, 'B', '1')

        // Enabling the auto promotion levels on the project
        ontrack.project(projectName).config {
            autoPromotionLevel()
        }

        // Promoting the builds in a different order
        [2, 4, 3, 1].each {
            String plName = plNames[it - 1]
            build.promote(plName)
        }

        // Gets the list of promotions for the branch and check this is the same order than for the predefined ones
        assert branch.promotionLevels.collect { it.name } == plNames
    }

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
        assert image.type == 'image/png'
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
        assert image.type == 'image/png'
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
