package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the build properties
 */
@AcceptanceTestSuite
class ACCDSLBuildProperties extends AbstractACCDSL {

    @Test
    void 'Build label'() {
        // Creating two builds with some build links
        def p = uid('P')

        ontrack.project(p) {
            branch('master') {
                build('1.0') {
                    config {
                        label 'v1.0'
                    }
                }
                build('2.0')
            }
        }

        assert ontrack.build(p, "master", "1.0").config.label == "v1.0"
        assert ontrack.build(p, "master", "2.0").config.label == null
    }


}
