package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for some template synchronisation use cases
 */
@AcceptanceTestSuite
class ACCDSLTemplateSync extends AbstractACCDSL {

    @Test
    void 'Instance sync must preserve the validation stamps'() {
        // Project and branch template
        def project = uid('P')
        ontrack.project(project) {
            branch('template') {
                template {
                    parameter 'paramName', 'A parameter'
                }
                validationStamp 'VS1'
                validationStamp 'VS2'
            }
        }
        // Instantiates the template into a branch
        ontrack.branch(project, 'template').instance('TEST', [paramName: 'paramValue'])
        // ... and checks it has its validation stamps
        assert ontrack.branch(project, 'TEST').type == 'TEMPLATE_INSTANCE'
        assert ontrack.branch(project, 'TEST').validationStamps*.name == ['VS1', 'VS2']

        // Now, update the project for auto validation stamps...
        ontrack.project(project).config {
            autoValidationStamp()
        }

        // Removes the validation stamps of the template
        ontrack.validationStamp(project, 'template', 'VS1').delete()
        ontrack.validationStamp(project, 'template', 'VS2').delete()

        // Checks they are gone
        assert ontrack.branch(project, 'template').validationStamps.empty

        // Syncs the instance
        ontrack.branch(project, 'TEST').syncInstance()

        // Cheks the existing validation stamps are still there
        assert ontrack.branch(project, 'TEST').type == 'TEMPLATE_INSTANCE'
        assert ontrack.branch(project, 'TEST').validationStamps*.name == ['VS1', 'VS2']
    }

    @Test
    void 'Instance sync must preserve the promotion levels'() {
        // Project and branch template
        def project = uid('P')
        ontrack.project(project) {
            branch('template') {
                template {
                    parameter 'paramName', 'A parameter'
                }
                promotionLevel 'PL1'
                promotionLevel 'PL2'
            }
        }
        // Instantiates the template into a branch
        ontrack.branch(project, 'template').instance('TEST', [paramName: 'paramValue'])
        // ... and checks it has its promotion levels
        assert ontrack.branch(project, 'TEST').type == 'TEMPLATE_INSTANCE'
        assert ontrack.branch(project, 'TEST').promotionLevels*.name == ['PL1', 'PL2']

        // Now, update the project for auto promotion levels...
        ontrack.project(project).config {
            autoPromotionLevel()
        }

        // Removes the promotion levels of the template
        ontrack.promotionLevel(project, 'template', 'PL1').delete()
        ontrack.promotionLevel(project, 'template', 'PL2').delete()

        // Checks they are gone
        assert ontrack.branch(project, 'template').promotionLevels.empty

        // Syncs the instance
        ontrack.branch(project, 'TEST').syncInstance()

        // Cheks the existing promotion levels are still there
        assert ontrack.branch(project, 'TEST').type == 'TEMPLATE_INSTANCE'
        assert ontrack.branch(project, 'TEST').promotionLevels*.name == ['PL1', 'PL2']
    }

    @Test
    void 'Template connection must preserve the validation stamps'() {
        // Project and branch template
        def project = uid('P')
        ontrack.project(project) {
            config {
                autoValidationStamp()
            }
            branch('template') {
                template {
                    parameter 'paramName', 'A parameter'
                }
            }
        }

        // Creates a standalone branch
        ontrack.project(project).branch('TEST')
        // Creates validation stamps
        ontrack.branch(project, 'TEST').with {
            validationStamp 'VS1'
            validationStamp 'VS2'
        }
        // ... and checks it has its validation stamps
        assert ontrack.branch(project, 'TEST').type == 'CLASSIC'
        assert ontrack.branch(project, 'TEST').validationStamps*.name == ['VS1', 'VS2']

        // Links to the template
        ontrack.branch(project, 'TEST').link('template', true, [paramName: 'paramValue'])

        // Cheks the existing validation stamps are still there
        assert ontrack.branch(project, 'TEST').type == 'TEMPLATE_INSTANCE'
        assert ontrack.branch(project, 'TEST').validationStamps*.name == ['VS1', 'VS2']
    }

    @Test
    void 'Template connection must preserve the promotion levels'() {
        // Project and branch template
        def project = uid('P')
        ontrack.project(project) {
            config {
                autoPromotionLevel()
            }
            branch('template') {
                template {
                    parameter 'paramName', 'A parameter'
                }
            }
        }

        // Creates a standalone branch
        ontrack.project(project).branch('TEST')
        // Creates promotion levels
        ontrack.branch(project, 'TEST').with {
            promotionLevel 'PL1'
            promotionLevel 'PL2'
        }
        // ... and checks it has its promotion levels
        assert ontrack.branch(project, 'TEST').type == 'CLASSIC'
        assert ontrack.branch(project, 'TEST').promotionLevels*.name == ['PL1', 'PL2']

        // Links to the template
        ontrack.branch(project, 'TEST').link('template', true, [paramName: 'paramValue'])

        // Cheks the existing promotion levels are still there
        assert ontrack.branch(project, 'TEST').type == 'TEMPLATE_INSTANCE'
        assert ontrack.branch(project, 'TEST').promotionLevels*.name == ['PL1', 'PL2']
    }

}
