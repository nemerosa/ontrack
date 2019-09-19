package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid
import static org.junit.Assert.assertEquals

/**
 * Acceptance tests for the extraction of links in the description of a validation run status
 */
@AcceptanceTestSuite
class ACCDSLValidationRunStatusLinks extends AbstractACCDSL {

    @Test
    void 'JIRA links in description of status'() {

        def jiraName = uid('J')
        def gitName = uid('G')
        ontrack.configure {
            jira jiraName, 'http://jira'
            git gitName, remote: 'https://github.com/nemerosa/ontrack.git', user: 'test', password: 'secret', issueServiceConfigurationIdentifier: "jira//${jiraName}"
        }

        def projectName = uid("P")
        def runId
        ontrack.project(projectName) {
            config {
                git(gitName)
            }
            branch("master") {
                validationStamp("VS")
                build("1.0.0") {
                    // First validation
                    validate("VS", "FAILED", "")
                    // Second validation with a link
                    runId = validate("VS", "DEFECTIVE", "Found PRJ-1234").id
                }
            }
        }

        def result = ontrack.graphQLQuery("""{
            validationRuns(id: $runId) {
                validationRunStatuses {
                    description
                    annotatedDescription
                }
            }
        }""")

        def validationRun = result.data.validationRuns.get(0)
        def validationRunStatuses = validationRun.validationRunStatuses
        def descriptions = validationRunStatuses.collect { it.description }
        def annotatedDescriptions = validationRunStatuses.collect { it.annotatedDescription }

        assertEquals(
                ["Found PRJ-1234"],
                descriptions
        )

        assertEquals(
                ["""Found <a href="http://jira/browse/PRJ-1234">PRJ-1234</a>"""],
                annotatedDescriptions
        )
    }

}
