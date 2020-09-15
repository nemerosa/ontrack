package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the auto promotion
 */
@AcceptanceTestSuite
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

    @Test
    void 'Auto promotion based on list, include and exclude patterns'() {

        // Creating a project and a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B', '')
        }
        def branch = ontrack.branch(projectName, 'B')

        // Creating validation stamps
        branch {
            validationStamp 'CI.1'
            validationStamp 'CI.2'
            validationStamp 'CI.NIGHTLY'
            validationStamp 'QA'
        }

        // Creation of auto promoted promotion levels
        branch {
            promotionLevel('COPPER') {
                config {
                    autoPromotion([], 'CI.*', '.*NIGHTLY.*')
                }
            }
            promotionLevel('BRONZE') {
                config {
                    autoPromotion(['QA'], 'CI.*', '.*NIGHTLY.*')
                }
            }
        }

        // Creating a build
        def build = branch.build('1')

        // CI validations first
        build.validate 'CI.1'
        build.validate 'CI.2'

        // Checks COPPER is OK
        // Checks BRONZE is NOK
        def promotionRuns = build.promotionRuns
        assert promotionRuns.collect { it.promotionLevel.name } == ['COPPER']

        // QA validation
        build.validate 'QA'

        // Checks COPPER is OK
        // Checks BRONZE is OK
        promotionRuns = build.promotionRuns
        assert promotionRuns.size() == 2
        assert promotionRuns.collect { it.promotionLevel.name } == ['BRONZE', 'COPPER']

    }

    @Test
    void 'Auto promotion based on promotion'() {

        // Creating a project and a branch
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B', '')
        }
        def branch = ontrack.branch(projectName, 'B')

        // Creating a a promotion level
        branch {
            promotionLevel("SILVER")
        }

        // Creating an auto promoted promotion level
        branch {
            promotionLevel("GOLD") {
                config {
                    autoPromotion([], "", "", ["SILVER"])
                }
            }
        }

        // Creating a build
        def build = branch.build('1')

        // Promotion 3 --> promoted
        build.promote('SILVER')
        def promotionRuns = build.promotionRuns
        assert promotionRuns.size() == 2
        assert promotionRuns*.promotionLevel.name ==['GOLD', 'SILVER']

    }

}
