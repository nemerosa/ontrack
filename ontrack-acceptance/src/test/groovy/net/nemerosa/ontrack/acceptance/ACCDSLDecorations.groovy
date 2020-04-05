package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the access to the decorations
 */
@AcceptanceTestSuite
class ACCDSLDecorations extends AbstractACCDSL {

    @Test
    void 'Accessing the image of a decoration'() {
        String path = 'extension/general/decoration/net.nemerosa.ontrack.extension.general.ReleaseDecorationExtension/release.png'
        // Accesses the raw content of the image
        def document = jsonClient.download(path)
        // Checks the document
        assert document != null
        assert document.type == 'image/png'
        assert document.content.length == 853
    }

    @Test
    void 'Access to the decoration images'() {
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('B') {
                validationStamp 'VS'
                (1..5).each {
                    build "${it}"
                }
            }
        }
        (1..5).each {
            ontrack.build(projectName, 'B', "${it}").validate 'VS', 'PASSED'
        }
    }

    @Test
    void 'Auto promotion property decoration for an auto promotion level'() {

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

        // Creating a normal promotion level
        branch {
            promotionLevel('PLN')
        }

        // Gets the decoration on the promotion level
        assert ontrack.promotionLevel(projectName, 'B', 'PL').autoPromotionPropertyDecoration.booleanValue()
        assert ontrack.promotionLevel(projectName, 'B', 'PLN').autoPromotionPropertyDecoration == null
    }

    @Test
    void 'Release decoration on a build'() {
        // Creating a branch
        def project = uid('P')
        ontrack.project(project).branch('1.0')
        // Creating a build
        def build = ontrack.branch(project, '1.0').build('1', 'Build 1')
        // No decoration
        assert build.releaseDecoration == null
        // Setting the Label property
        build.config {
            label 'RC'
        }
        // Decoration
        assert build.releaseDecoration == 'RC'
    }

    @Test
    void 'Message decorations on projects, branches and builds'() {
        def name = uid('P')
        ontrack.project(name) {
            branch('test') {
                build('1')
            }
        }
        def project = ontrack.project(name)
        def branch = ontrack.branch(name, 'test')
        def build = ontrack.build(name, 'test', '1')
        // No decorations yet
        assert project.messageDecoration == null
        assert branch.messageDecoration == null
        assert build.messageDecoration == null
        // Project decorations
        project.config.message('Information', 'INFO')
        assert project.messageDecoration == [type: 'INFO', text: 'Information']
        // Branch decorations
        branch.config.message('Warning', 'WARNING')
        assert branch.messageDecoration == [type: 'WARNING', text: 'Warning']
        // Build decorations
        build.config.message('Error', 'ERROR')
        assert build.messageDecoration == [type: 'ERROR', text: 'Error']
    }

}
