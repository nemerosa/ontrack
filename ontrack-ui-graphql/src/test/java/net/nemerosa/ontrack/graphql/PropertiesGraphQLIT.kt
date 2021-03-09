package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.extension.general.MessagePropertyType
import net.nemerosa.ontrack.extension.general.MessageType
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.typeName
import net.nemerosa.ontrack.model.structure.varName
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PropertiesGraphQLIT : AbstractQLKTITSupport() {

    @Test
    fun `Edition of property by ID`() {
        asAdmin {
            project {
                testPropertyEditionById()
                branch {
                    testPropertyEditionById()
                    val build = build {
                        testPropertyEditionById()
                    }
                    val vs = validationStamp().apply { testPropertyEditionById() }
                    val pl = promotionLevel { testPropertyEditionById() }
                    build.validate(vs).apply { testPropertyEditionById() }
                    build.promote(pl).apply { testPropertyEditionById() }
                }
            }
        }
    }

    companion object {
        const val testPropertyName = "net.nemerosa.ontrack.extension.general.MessagePropertyType"
    }

    private fun ProjectEntity.testPropertyEditionById() {
        run("""
            mutation {
                set${projectEntityType.typeName}Property(input: {id: $id, property: "$testPropertyName", value: {type: "INFO", text: "My message"}}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """).let { data ->
            val nodeName = "set${projectEntityType.typeName}Property"
            assertNoUserError(data, nodeName)
            val returnedEntityId = data.path(nodeName).path(projectEntityType.varName).path("id").asInt()
            assertEquals(this.id(), returnedEntityId)

            val property = getProperty(this, MessagePropertyType::class.java)
            assertNotNull(property) {
                assertEquals(MessageType.INFO, property.type)
                assertEquals("My message", property.text)
            }
        }

        // Deleting the property
        run("""
            mutation {
                set${projectEntityType.typeName}Property(input: {id: $id, property: "$testPropertyName", value: null}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """).let { data ->
            val nodeName = "set${projectEntityType.typeName}Property"
            assertNoUserError(data, nodeName)
            val returnedEntityId = data.path(nodeName).path(projectEntityType.varName).path("id").asInt()
            assertEquals(this.id(), returnedEntityId)

            val property = getProperty(this, MessagePropertyType::class.java)
            assertNull(property)
        }
    }

    @Test
    fun `Properties for a project`() {
        asAdmin {
            run("""{
                properties(projectEntityType: PROJECT) {
                    typeName
                    name
                    description
                    supportedEntityTypes
                }
            }""").let { data ->
                val properties = data.path("properties")
                // Checks we find the message property
                assertNotNull(properties.find {
                    it.path("typeName").asText() == "net.nemerosa.ontrack.extension.general.MessagePropertyType"
                }) { property ->
                    assertEquals(
                        "net.nemerosa.ontrack.extension.general.MessagePropertyType",
                        property.path("typeName").asText())
                    assertEquals("Message", property.path("name").asText())
                    assertEquals("Message.", property.path("description").asText())
                    assertEquals(
                        ProjectEntityType.values().map { it.name }.toSet(),
                        property.path("supportedEntityTypes").map { it.asText() }.toSet()
                    )
                }
                // Checks we find the build links display property
                assertNotNull(properties.find {
                    it.path("typeName")
                        .asText() == "net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType"
                }) { property ->
                    assertEquals(
                        "net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType",
                        property.path("typeName").asText())
                    assertEquals("Build link display options", property.path("name").asText())
                    assertEquals("Configuration of display options for the build links towards this project.",
                        property.path("description").asText())
                    assertEquals(
                        setOf(ProjectEntityType.PROJECT.name),
                        property.path("supportedEntityTypes").map { it.asText() }.toSet()
                    )
                }
                // Checks we DON'T find the promotion dependencies property
                assertNull(properties.find {
                    it.path("typeName")
                        .asText() == "net.nemerosa.ontrack.extension.general.PromotionDependenciesPropertyType"
                })
            }
        }
    }

    @Test
    fun `Property by type`() {
        asAdmin {
            run("""{
                properties(type: "net.nemerosa.ontrack.extension.general.MessagePropertyType") {
                    typeName
                    name
                    description
                    supportedEntityTypes
                }
            }""").let { data ->
                val properties = data.path("properties")
                assertEquals(1, properties.size())
                val property = properties.first()
                // Checks we find the message property
                assertEquals(
                    "net.nemerosa.ontrack.extension.general.MessagePropertyType",
                    property.path("typeName").asText())
                assertEquals("Message", property.path("name").asText())
                assertEquals("Message.", property.path("description").asText())
                assertEquals(
                    ProjectEntityType.values().map { it.name }.toSet(),
                    property.path("supportedEntityTypes").map { it.asText() }.toSet()
                )
            }
        }
    }
}