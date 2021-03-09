package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PropertiesGraphQLIT : AbstractQLKTITSupport() {

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
                    it.path("typeName").asText() == "net.nemerosa.ontrack.extension.general.BuildLinkDisplayPropertyType"
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