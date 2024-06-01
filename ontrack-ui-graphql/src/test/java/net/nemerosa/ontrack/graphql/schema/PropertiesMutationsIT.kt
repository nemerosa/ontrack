package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

}