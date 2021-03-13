package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.config
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ThresholdPercentageValidationDataTypeGraphQLMutationIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var thresholdPercentageValidationDataType: ThresholdPercentageValidationDataType

    @Test
    fun `Creation of a percentage validation stamp`() {
        asAdmin {
            project {
                branch {
                    run("""
                        mutation {
                            setupPercentageValidationStamp(input: {
                                project: "${project.name}",
                                branch: "$name",
                                validation: "test",
                                warningThreshold: 10,
                                failureThreshold: 20,
                                okIfGreater: false
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
                        val node = assertNoUserError(data, "setupPercentageValidationStamp")
                        assertTrue(node.path("validationStamp").path("id").asInt() != 0, "VS created")

                        assertPresent(structureService.findValidationStampByName(project.name, name, "test")) {
                            assertEquals("test", it.name)
                            assertEquals("net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType",
                                it.dataType?.descriptor?.id)
                            assertEquals(
                                ThresholdConfig(
                                    warningThreshold = 10,
                                    failureThreshold = 20,
                                    okIfGreater = false
                                ),
                                it.dataType?.config
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Update of a percentage validation stamp`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp(
                        validationDataTypeConfig = thresholdPercentageValidationDataType.config(
                            ThresholdConfig(
                                warningThreshold = 0,
                                failureThreshold = 50,
                                okIfGreater = false
                            )
                        ))
                    run("""
                        mutation {
                            setupPercentageValidationStamp(input: {
                                project: "${project.name}",
                                branch: "$name",
                                validation: "${vs.name}",
                                warningThreshold: 10,
                                failureThreshold: 20,
                                okIfGreater: false
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
                        val node = assertNoUserError(data, "setupPercentageValidationStamp")
                        assertEquals(vs.id(), node.path("validationStamp").path("id").asInt(), "VS updated")

                        assertPresent(structureService.findValidationStampByName(project.name, name, vs.name)) {
                            assertEquals(vs.name, it.name)
                            assertEquals("net.nemerosa.ontrack.extension.general.validation.ThresholdPercentageValidationDataType",
                                it.dataType?.descriptor?.id)
                            assertEquals(
                                ThresholdConfig(
                                    warningThreshold = 10,
                                    failureThreshold = 20,
                                    okIfGreater = false
                                ),
                                it.dataType?.config
                            )
                        }
                    }
                }
            }
        }
    }
}