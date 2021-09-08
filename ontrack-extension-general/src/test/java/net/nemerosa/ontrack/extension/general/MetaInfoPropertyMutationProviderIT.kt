package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.nameValues
import net.nemerosa.ontrack.model.structure.typeName
import net.nemerosa.ontrack.model.structure.varName
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class MetaInfoPropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting the meta info property at different levels`() {
        multiLevelTest {
            testMetaInfoPropertyById(update = false)
            testMetaInfoPropertyById(update = true)
        }
    }

    @Test
    fun `Setting the meta info property using names at different levels`() {
        multiLevelTest {
            testMetaInfoPropertyByName(update = false)
            testMetaInfoPropertyByName(update = true)
        }
    }

    private fun ProjectEntity.testMetaInfoPropertyById(update: Boolean) {

        if (update) {
            setProperty(
                this, MetaInfoPropertyType::class.java, MetaInfoProperty(
                    listOf(
                        MetaInfoPropertyItem("name-0", "value-0", null, null)
                    )
                )
            )
        }

        run(
            """
            mutation {
                set${projectEntityType.typeName}MetaInfoPropertyById(input: {id: $id, append: $update, items: [{name: "meta-name", value: "meta-value", link: "meta-link", category: "meta-category"}]}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """
        ).let { data ->
            checkGraphQLUserErrors(data, "set${projectEntityType.typeName}MetaInfoPropertyById") { payload ->
                val returnedEntityId = payload.path(projectEntityType.varName).path("id").asInt()
                assertEquals(this.id(), returnedEntityId)
                val property = getProperty(this, MetaInfoPropertyType::class.java)
                assertNotNull(property) {
                    assertEquals(
                        if (update) {
                            listOf(
                                MetaInfoPropertyItem("name-0", "value-0", null, null),
                                MetaInfoPropertyItem("meta-name", "meta-value", "meta-link", "meta-category"),
                            )
                        } else {
                            listOf(
                                MetaInfoPropertyItem("meta-name", "meta-value", "meta-link", "meta-category"),
                            )
                        },
                        it.items
                    )
                }
            }
        }

        // Deleting the property
        run(
            """
            mutation {
                delete${projectEntityType.typeName}MetaInfoPropertyById(input: {id: $id}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """
        ).let { data ->
            checkGraphQLUserErrors(data, "delete${projectEntityType.typeName}MetaInfoPropertyById") { payload ->
                val returnedEntityId = payload.path(projectEntityType.varName).path("id").asInt()
                assertEquals(this.id(), returnedEntityId)

                val property = getProperty(this, MetaInfoPropertyType::class.java)
                assertNull(property)
            }
        }
    }

    private fun ProjectEntity.testMetaInfoPropertyByName(update: Boolean) {

        val input = nameValues.map { (field, value) -> """$field: "$value"""" }.joinToString(", ")

        if (update) {
            setProperty(
                this, MetaInfoPropertyType::class.java, MetaInfoProperty(
                    listOf(
                        MetaInfoPropertyItem("name-0", "value-0", null, null)
                    )
                )
            )
        }

        run(
            """
            mutation {
                set${projectEntityType.typeName}MetaInfoProperty(input: {$input, append: $update, items: [{name: "meta-name", value: "meta-value", link: "meta-link", category: "meta-category"}]}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """
        ).let { data ->
            checkGraphQLUserErrors(data, "set${projectEntityType.typeName}MetaInfoProperty") { payload ->
                val returnedEntityId = payload.path(projectEntityType.varName).path("id").asInt()
                assertEquals(this.id(), returnedEntityId)
                val property = getProperty(this, MetaInfoPropertyType::class.java)
                assertNotNull(property) {
                    assertEquals(
                        if (update) {
                            listOf(
                                MetaInfoPropertyItem("name-0", "value-0", null, null),
                                MetaInfoPropertyItem("meta-name", "meta-value", "meta-link", "meta-category"),
                            )
                        } else {
                            listOf(
                                MetaInfoPropertyItem("meta-name", "meta-value", "meta-link", "meta-category"),
                            )
                        },
                        it.items
                    )
                }
            }
        }

        // Deleting the property
        run(
            """
            mutation {
                delete${projectEntityType.typeName}MetaInfoProperty(input: {$input}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """
        ).let { data ->
            checkGraphQLUserErrors(data, "delete${projectEntityType.typeName}MetaInfoProperty") { payload ->
                val returnedEntityId = payload.path(projectEntityType.varName).path("id").asInt()
                assertEquals(this.id(), returnedEntityId)

                val property = getProperty(this, MetaInfoPropertyType::class.java)
                assertNull(property)
            }
        }
    }

}