package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the auto promotion
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
class ACCDSLAutoPromotion extends AbstractACCDSL {

    @Test
    void 'Auto promotion'() {

        // Creating a project and a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B', '')
        }
        def branch = ontrack.branch(projectName, 'B')

        // Creating 3 validation stamps
        (1..3).each { no ->
            branch {
                validationStamp "VS${no}"
            }
        }

        // Creating an auto promoted promotion level
        branch {
            promotionLevel('PL') {
                config {
                    autoPromotion 'VS2', 'VS3'
                }
            }
        }

        // Creating a build
        def build = branch.build('1')

        // Validation 1 --> no promotion
        build.validate('VS1', 'PASSED')
        assert build.promotionRuns.size() == 0

        // Validation 2 --> no promotion (yet)
        build.validate('VS2', 'PASSED')
        assert build.promotionRuns.size() == 0

        // Validation 3 --> promoted
        build.validate('VS3', 'PASSED')
        def promotionRuns = build.promotionRuns
        assert promotionRuns.size() == 1
        assert promotionRuns[0].promotionLevel.name == 'PL'

    }

    @Test
    void 'Auto promotion on passed only'() {

        // Creating a project and a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B', '')
        }
        def branch = ontrack.branch(projectName, 'B')

        // Creating 1 validation stamp
        branch {
            validationStamp 'VS1'
        }

        // Creating an auto promoted promotion level
        branch {
            promotionLevel('PL') {
                config {
                    autoPromotion 'VS1'
                }
            }
        }

        // Creating a build
        def build = branch.build('1')

        // Validation failed --> no promotion
        build.validate('VS1', 'FAILED')
        assert build.promotionRuns.size() == 0

        // Validation passed --> promoted
        build.validate('VS1', 'PASSED')
        def promotionRuns = build.promotionRuns
        assert promotionRuns.size() == 1
        assert promotionRuns[0].promotionLevel.name == 'PL'

    }

}
