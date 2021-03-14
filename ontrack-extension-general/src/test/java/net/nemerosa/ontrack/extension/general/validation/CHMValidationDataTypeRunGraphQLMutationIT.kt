package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.config
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.test.assertEquals

@Component
class CHMValidationDataTypeRunGraphQLMutationIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var chmlValidationDataType: CHMLValidationDataType

    @Test
    fun `Passed validation CHML`() {
        testValidationByName(medium = 1, expectedStatus = "PASSED")
    }

    @Test
    fun `Warning validation CHML`() {
        testValidationByName(high = 1, expectedStatus = "WARNING")
    }

    @Test
    fun `Failed validation CHML`() {
        testValidationByName(critical = 1, expectedStatus = "FAILED")
    }

    private fun testValidationByName(
        critical: Int = 0,
        high: Int = 0,
        medium: Int = 0,
        expectedStatus: String,
    ) {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp(
                        validationDataTypeConfig = chmlValidationDataType.config(
                            CHMLValidationDataTypeConfig(
                                warningLevel = CHMLLevel(CHML.HIGH, 1),
                                failedLevel = CHMLLevel(CHML.CRITICAL, 1),
                            )
                        )
                    )
                    build {
                        run("""
                            mutation {
                                validateBuildWithCHML(input: {
                                    project: "${project.name}",
                                    branch: "${branch.name}",
                                    build: "$name",
                                    validation: "${vs.name}",
                                    critical: $critical,
                                    high: $high,
                                    medium: $medium
                                }) {
                                    validationRun {
                                        validationStamp {
                                            name
                                        }
                                        data {
                                            descriptor {
                                                id
                                            }
                                            data
                                        }
                                        validationRunStatuses {
                                            statusID {
                                                id
                                            }
                                        }
                                    }
                                    errors {
                                        message
                                    }
                                }
                            }
                        """).let { data ->
                            val node = assertNoUserError(data, "validateBuildWithCHML")
                            val run = node.path("validationRun")

                            assertEquals(vs.name, run.path("validationStamp").path("name").asText())
                            val runData = run.path("data")
                            assertEquals(CHMLValidationDataType::class.java.name,
                                runData.path("descriptor").path("id").asText())
                            assertEquals(
                                mapOf(
                                    "CRITICAL" to critical,
                                    "HIGH" to high,
                                    "MEDIUM" to medium,
                                    "LOW" to 0,
                                ).asJson(),
                                runData.path("data").path("levels")
                            )
                            val status = run.path("validationRunStatuses").path(0).path("statusID").path("id").asText()
                            assertEquals(expectedStatus, status)
                        }
                    }
                }
            }
        }
    }

}