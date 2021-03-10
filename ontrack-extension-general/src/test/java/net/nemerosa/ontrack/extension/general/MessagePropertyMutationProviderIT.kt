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

class MessagePropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting the message property at different levels`() {
        multiLevelTest {
            testMessagePropertyById()
        }
    }

    @Test
    fun `Setting the message property using names at different levels`() {
        multiLevelTest {
            testMessagePropertyByName()
        }
    }

    private fun ProjectEntity.testMessagePropertyById() {
        run("""
            mutation {
                set${projectEntityType.typeName}MessagePropertyById(input: {id: $id, type: "INFO", text: "My message"}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """).let { data ->
            val nodeName = "set${projectEntityType.typeName}MessagePropertyById"
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
                delete${projectEntityType.typeName}MessagePropertyById(input: {id: $id}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """).let { data ->
            val nodeName = "delete${projectEntityType.typeName}MessagePropertyById"
            assertNoUserError(data, nodeName)
            val returnedEntityId = data.path(nodeName).path(projectEntityType.varName).path("id").asInt()
            assertEquals(this.id(), returnedEntityId)

            val property = getProperty(this, MessagePropertyType::class.java)
            assertNull(property)
        }
    }

    private fun ProjectEntity.testMessagePropertyByName() {
        val input = nameValues.map { (field, value) -> """$field: "$value""""}.joinToString(", ")
        run("""
            mutation {
                set${projectEntityType.typeName}MessageProperty(input: {$input, type: "INFO", text: "My message"}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """).let { data ->
            val nodeName = "set${projectEntityType.typeName}MessageProperty"
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
                delete${projectEntityType.typeName}MessageProperty(input: {$input}) {
                    ${projectEntityType.varName} {
                        id
                    }
                    errors {
                        message
                    }
                }
            }
        """).let { data ->
            val nodeName = "delete${projectEntityType.typeName}MessageProperty"
            assertNoUserError(data, nodeName)
            val returnedEntityId = data.path(nodeName).path(projectEntityType.varName).path("id").asInt()
            assertEquals(this.id(), returnedEntityId)

            val property = getProperty(this, MessagePropertyType::class.java)
            assertNull(property)
        }
    }

}