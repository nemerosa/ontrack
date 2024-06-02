package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.extension.api.support.TestConfiguration
import net.nemerosa.ontrack.extension.api.support.TestProperty
import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PropertiesMutationsIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting a property using the generic mutation`() {
        asAdmin {
            project {
                run(
                    """
                    mutation {
                        setGenericProperty(input: {
                            entityType: PROJECT,
                            entityId: $id,
                            type: "${TestPropertyType::class.java.name}",
                            value: {
                                configuration: "test-config",
                                value: "test-value"
                            }
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
                )
                val property = propertyService.getPropertyValue(this, TestPropertyType::class.java)
                assertNotNull(property) {
                    assertEquals("test-config", it.configuration.name)
                    assertEquals("test-value", it.value)
                }
            }
        }
    }

    @Test
    fun `Deleting a property using the generic mutation`() {
        asAdmin {
            project {
                propertyService.editProperty(
                    this,
                    TestPropertyType::class.java,
                    TestProperty(
                        configuration = TestConfiguration("test-config", "user", "xxx"),
                        value = "test-value"
                    )
                )
                run(
                    """
                    mutation {
                        deleteGenericProperty(input: {
                            entityType: PROJECT,
                            entityId: $id,
                            type: "${TestPropertyType::class.java.name}"
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
                )
                val property = propertyService.getPropertyValue(this, TestPropertyType::class.java)
                assertNull(property, "Property has been deleted")
            }
        }
    }

}