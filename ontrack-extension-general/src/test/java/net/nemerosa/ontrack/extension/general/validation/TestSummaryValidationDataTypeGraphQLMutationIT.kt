package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.config
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestSummaryValidationDataTypeGraphQLMutationIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var testSummaryValidationDataType: TestSummaryValidationDataType

    @Test
    fun `Creation of a test summary validation stamp`() {
        asAdmin {
            project {
                branch {
                    run("""
                        mutation {
                            setupTestSummaryValidationStamp(input: {
                                project: "${project.name}",
                                branch: "$name",
                                validation: "test",
                                warningIfSkipped: true
                            }) {
                                validationStamp {
                                    id
                                }
                                errors {
                                    message
                                }
                            }
                        }
                    """).let { data ->
                        val node = assertNoUserError(data, "setupTestSummaryValidationStamp")
                        assertTrue(node.path("validationStamp").path("id").asInt() != 0, "VS created")

                        assertPresent(structureService.findValidationStampByName(project.name, name, "test")) {
                            assertEquals("test", it.name)
                            assertEquals("net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType",
                                it.dataType?.descriptor?.id)
                            assertEquals(
                                TestSummaryValidationConfig(warningIfSkipped = true),
                                it.dataType?.config
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Update of a test summary validation stamp`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp(
                        validationDataTypeConfig = testSummaryValidationDataType.config(
                            TestSummaryValidationConfig(
                                warningIfSkipped = false
                            )
                        ))
                    run("""
                        mutation {
                            setupTestSummaryValidationStamp(input: {
                                project: "${project.name}",
                                branch: "$name",
                                validation: "${vs.name}",
                                warningIfSkipped: true
                            }) {
                                validationStamp {
                                    id
                                }
                                errors {
                                    message
                                }
                            }
                        }
                    """).let { data ->
                        val node = assertNoUserError(data, "setupTestSummaryValidationStamp")
                        assertEquals(vs.id(), node.path("validationStamp").path("id").asInt(), "VS updated")

                        assertPresent(structureService.findValidationStampByName(project.name, name, vs.name)) {
                            assertEquals(vs.name, it.name)
                            assertEquals("net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType",
                                it.dataType?.descriptor?.id)
                            assertEquals(
                                TestSummaryValidationConfig(warningIfSkipped = true),
                                it.dataType?.config
                            )
                        }
                    }
                }
            }
        }
    }
}