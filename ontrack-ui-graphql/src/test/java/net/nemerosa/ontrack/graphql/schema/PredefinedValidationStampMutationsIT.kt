package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.extension.general.validation.*
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PredefinedValidationStampMutationsIT : AbstractQLKTITSupport() {

    @Test
    fun `Creating a predefined validation stamp`() {
        asAdmin {
            val name = uid("pvs_")
            run(
                """
                    mutation {
                        createPredefinedValidationStamp(input: {
                            name: "$name",
                            description: "Some text"
                        }) {
                            predefinedValidationStamp {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "createPredefinedValidationStamp") { node ->
                    val id = node.path("predefinedValidationStamp").path("id").asInt()
                    assertEquals(
                        name,
                        predefinedValidationStampService.getPredefinedValidationStamp(ID.of(id)).name
                    )
                    assertEquals(
                        id,
                        predefinedValidationStampService.findPredefinedValidationStampByName(name)?.id()
                    )
                }
            }
        }
    }

    @Test
    fun `Creating a predefined validation stamp with a data type`() {
        asAdmin {
            val name = uid("pvs_")
            run(
                """
                    mutation {
                        createPredefinedValidationStamp(input: {
                            name: "$name",
                            description: "Some text"
                            dataType: "${TestSummaryValidationDataType::class.java.name}",
                            dataTypeConfig: {
                                warningIfSkipped: false,
                            },
                        }) {
                            predefinedValidationStamp {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "createPredefinedValidationStamp") { node ->
                    val id = node.path("predefinedValidationStamp").path("id").asInt()
                    assertEquals(
                        name,
                        predefinedValidationStampService.getPredefinedValidationStamp(ID.of(id)).name
                    )
                    assertNotNull(predefinedValidationStampService.findPredefinedValidationStampByName(name)) {
                        assertEquals(id, it.id())
                        assertNotNull(it.dataType) { dataType ->
                            assertEquals(
                                TestSummaryValidationDataType::class.java.name,
                                dataType.descriptor.id,
                            )
                            assertEquals(
                                mapOf(
                                    "warningIfSkipped" to false,
                                ).asJson(),
                                dataType.config.asJson()
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Updating a predefined validation stamp`() {
        asAdmin {
            val pvs = predefinedValidationStamp()
            val name = uid("pvs_")
            run(
                """
                    mutation {
                        updatePredefinedValidationStamp(input: {
                            id: ${pvs.id},
                            name: "$name",
                            description: "Some text"
                        }) {
                            predefinedValidationStamp {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "updatePredefinedValidationStamp") { node ->
                    val id = node.path("predefinedValidationStamp").path("id").asInt()
                    assertEquals(pvs.id(), id)
                    assertEquals(
                        name,
                        predefinedValidationStampService.getPredefinedValidationStamp(ID.of(id)).name
                    )
                    assertEquals(
                        id,
                        predefinedValidationStampService.findPredefinedValidationStampByName(name)?.id()
                    )
                }
            }
        }
    }

    @Test
    fun `Updating a predefined validation stamp with a data type`() {
        asAdmin {
            val pvs = predefinedValidationStamp()
            val name = uid("pvs_")
            run(
                """
                    mutation {
                        updatePredefinedValidationStamp(input: {
                            id: ${pvs.id},
                            name: "$name",
                            description: "Some text",
                            dataType: "${CHMLValidationDataType::class.java.name}",
                            dataTypeConfig: {
                                warningLevel: "HIGH",
                                warningValue: 1,
                                failedLevel: "CRITICAL",
                                failedValue: 1,
                            }
                        }) {
                            predefinedValidationStamp {
                                id
                            }
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "updatePredefinedValidationStamp") { node ->
                    val id = node.path("predefinedValidationStamp").path("id").asInt()
                    assertEquals(pvs.id(), id)
                    assertEquals(
                        name,
                        predefinedValidationStampService.getPredefinedValidationStamp(ID.of(id)).name
                    )
                    assertNotNull(predefinedValidationStampService.findPredefinedValidationStampByName(name)) {
                        assertEquals(id, it.id())
                        assertNotNull(it.dataType) { dataType ->
                            assertEquals(
                                CHMLValidationDataType::class.java.name,
                                dataType.descriptor.id,
                            )
                            assertEquals(
                                CHMLValidationDataTypeConfig(
                                    warningLevel = CHMLLevel(
                                        level = CHML.HIGH,
                                        value = 1,
                                    ),
                                    failedLevel = CHMLLevel(
                                        level = CHML.CRITICAL,
                                        value = 1,
                                    ),
                                ).asJson(),
                                dataType.config.asJson()
                            )
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Deleting a predefined validation stamp`() {
        asAdmin {
            val pvs = predefinedValidationStamp()
            run(
                """
                    mutation {
                        deletePredefinedValidationStamp(input: {
                            id: ${pvs.id}
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
            ) { data ->
                checkGraphQLUserErrors(data, "deletePredefinedValidationStamp")
                assertEquals(
                    null,
                    predefinedValidationStampService.findPredefinedValidationStampByName(pvs.name)
                )
            }
        }
    }

}