package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.dsl.v4.ValidationRun
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid
import static org.junit.Assert.assertEquals

/**
 * Acceptance tests for the `ValidationRun` DSL
 */
@AcceptanceTestSuite
class ACCDSLValidationRun extends AbstractACCDSL {

    @Test
    void 'Validation run status comment'() {
        def projectName = uid("P")
        ontrack.project(projectName) {
            branch("master") {
                validationStamp("VS")
                build("1.0.0") {
                    // Validation
                    ValidationRun run = validate("VS", "FAILED")
                    // No description first
                    assert run.lastValidationRunStatus.description == ""
                    // Sets the description
                    run.lastValidationRunStatus.description = "This is my description with a link to https://github.com/nemerosa/ontrack"
                    // Checks the final description
                    def result = ontrack.graphQLQuery("""{
                        validationRuns(id: ${run.id}) {
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
                            ["This is my description with a link to https://github.com/nemerosa/ontrack"],
                            descriptions
                    )

                    assertEquals(
                            ["""This is my description with a link to <a href="https://github.com/nemerosa/ontrack" target="_blank">https://github.com/nemerosa/ontrack</a>"""],
                            annotatedDescriptions
                    )
                }
            }
        }
    }

}
