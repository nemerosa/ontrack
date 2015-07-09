package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the build links
 */
@AcceptanceTestSuite
@AcceptanceTest(excludes = 'production')
class ACCDSLBuildLinks extends AbstractACCDSL {

    @Test
    void 'Build links'() {

        // Creating projects, branches and builds

        def p1 = uid('P1')
        ontrack.project(p1) {
            branch('B1', '') {
                build('1.0', '')
                build('1.1', '')
            }
        }

        def p2 = uid('P2')
        ontrack.project(p2) {
            branch('B2', '') {
                build('2.0', '')
                build('2.1', '')
            }
        }

        def p3 = uid('P3')

        // Build ids

        def b111 = ontrack.build(p1, 'B1', '1.1').id
        def b220 = ontrack.build(p2, 'B2', '2.0').id

        // Links
        ontrack.build(p1, 'B1', '1.0').config {
            // Same project
            buildLink p1, '1.1'
            // Other project
            buildLink p2, '2.0'
            // Unexisting build
            buildLink p2, '2.2'
            // Unexisting project
            buildLink p3, '3.0'
        }

        // Gets the links
        def buildLinks = ontrack.build(p1, 'B1', '1.0').config.buildLinks
        assert buildLinks.collect { [ it.project, it.build, it.page ] } == [
                [ p1, '1.1', "${baseURL}/#/build/${b111}" ],
                [ p2, '2.0', "${baseURL}/#/build/${b220}" ],
                [ p2, '2.2', null ],
                [ p3, '3.0', null ],
        ]

    }

}
