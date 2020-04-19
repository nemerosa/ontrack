package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Acceptance tests for the run info of builds and validation runs.
 */
@AcceptanceTestSuite
class ACCDSLRunInfo extends AbstractACCDSL {

    @Test
    void 'Build without run info'() {
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('1.0') {
                def build = build('1.0.0')
                // Gets the run info
                def info = build.runInfo
                assert info == null: "No run info"
            }
        }
    }

    @Test
    void 'Build with run info'() {
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('1.0') {
                def build = build('1.0.0')
                // Sets the run info
                build.setRunInfo sourceType: "jenkins",
                        sourceUri: "http://jenkins/job/build/1",
                        triggerType: "user",
                        triggerData: "damien",
                        runTime: 30
                // Gets the run info
                def info = build.runInfo
                assert info != null
                assert info.id != 0
                assert info.sourceType == "jenkins"
                assert info.sourceUri == "http://jenkins/job/build/1"
                assert info.triggerType == "user"
                assert info.triggerData == "damien"
                assert info.runTime == 30
            }
        }
    }

    @Test
    void 'Validation run without run info'() {
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('1.0') {
                validationStamp('VS')
                def build = build('1.0.0')
                def run = build.validate('VS')
                // Gets the run info
                def info = run.runInfo
                assert info == null: "No run info"
            }
        }
    }

    @Test
    void 'Validation run with run info'() {
        def projectName = uid('P')
        ontrack.project(projectName) {
            branch('1.0') {
                validationStamp('VS')
                def build = build('1.0.0')
                def run = build.validate('VS')
                // Sets the run info
                run.setRunInfo sourceType: "jenkins",
                        sourceUri: "http://jenkins/job/build/1",
                        triggerType: "user",
                        triggerData: "damien",
                        runTime: 30
                // Gets the run info
                def info = run.runInfo
                assert info != null
                assert info.id != 0
                assert info.sourceType == "jenkins"
                assert info.sourceUri == "http://jenkins/job/build/1"
                assert info.triggerType == "user"
                assert info.triggerData == "damien"
                assert info.runTime == 30
            }
        }
    }

}
