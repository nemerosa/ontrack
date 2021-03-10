package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.typeName
import net.nemerosa.ontrack.model.structure.varName
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MessagePropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting the message property at different levels`() {
        asAdmin {
            project {
                testMessagePropertyById()
                branch {
                    testMessagePropertyById()
                    val build = build {
                        testMessagePropertyById()
                    }
                    val vs = validationStamp().apply { testMessagePropertyById() }
                    val pl = promotionLevel { testMessagePropertyById() }
                    build.validate(vs).apply { testMessagePropertyById() }
                    build.promote(pl).apply { testMessagePropertyById() }
                }
            }
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

        // TODO Deleting the property
//        run("""
//            mutation {
//                set${projectEntityType.typeName}PropertyById(input: {id: $id, property: "${PropertiesGraphQLIT.testPropertyName}", value: null}) {
//                    ${projectEntityType.varName} {
//                        id
//                    }
//                    errors {
//                        message
//                    }
//                }
//            }
//        """).let { data ->
//            val nodeName = "set${projectEntityType.typeName}PropertyById"
//            assertNoUserError(data, nodeName)
//            val returnedEntityId = data.path(nodeName).path(projectEntityType.varName).path("id").asInt()
//            assertEquals(this.id(), returnedEntityId)
//
//            val property = getProperty(this, MessagePropertyType::class.java)
//            assertNull(property)
//        }
    }

}