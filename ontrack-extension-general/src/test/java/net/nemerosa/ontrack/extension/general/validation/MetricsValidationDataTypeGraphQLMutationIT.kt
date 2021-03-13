package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.config
import net.nemerosa.ontrack.test.assertPresent
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MetricsValidationDataTypeGraphQLMutationIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var metricsValidationDataType: MetricsValidationDataType

    @Test
    fun `Creation of a metrics validation stamp`() {
        asAdmin {
            project {
                branch {
                    run("""
                        mutation {
                            setupMetricsValidationStamp(input: {
                                project: "${project.name}",
                                branch: "$name",
                                validation: "test"
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
                        val node = assertNoUserError(data, "setupMetricsValidationStamp")
                        assertTrue(node.path("validationStamp").path("id").asInt() != 0, "VS created")

                        assertPresent(structureService.findValidationStampByName(project.name, name, "test")) {
                            assertEquals("test", it.name)
                            assertEquals("net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                                it.dataType?.descriptor?.id)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Update of a metrics validation stamp`() {
        asAdmin {
            project {
                branch {
                    val vs = validationStamp(
                        validationDataTypeConfig = metricsValidationDataType.config(null)
                    )
                    run("""
                        mutation {
                            setupMetricsValidationStamp(input: {
                                project: "${project.name}",
                                branch: "$name",
                                validation: "${vs.name}"
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
                        val node = assertNoUserError(data, "setupMetricsValidationStamp")
                        assertEquals(vs.id(), node.path("validationStamp").path("id").asInt(), "VS updated")

                        assertPresent(structureService.findValidationStampByName(project.name, name, vs.name)) {
                            assertEquals(vs.name, it.name)
                            assertEquals("net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                                it.dataType?.descriptor?.id)
                        }
                    }
                }
            }
        }
    }
}