package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class BuildLinkDisplayPropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting the property using the project ID`() {
        asAdmin {
            project {
                run(
                    """
                    mutation {
                        setProjectBuildLinkDisplayPropertyById(input: {
                            id: $id,
                            useLabel: true,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
                ) { data ->
                    assertNoUserError(data, "setProjectBuildLinkDisplayPropertyById")
                    val property = getProperty(this, BuildLinkDisplayPropertyType::class.java)
                    assertNotNull(property, "Property has been set") {
                        assertEquals(true, it.useLabel, "Use label has been set to true")
                    }
                }

            }
        }
    }

    @Test
    fun `Setting the property using the project name`() {
        asAdmin {
            project {
                run(
                    """
                    mutation {
                        setProjectBuildLinkDisplayProperty(input: {
                            project: "$name",
                            useLabel: true,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """
                ) { data ->
                    assertNoUserError(data, "setProjectBuildLinkDisplayProperty")
                    val property = getProperty(this, BuildLinkDisplayPropertyType::class.java)
                    assertNotNull(property, "Property has been set") {
                        assertEquals(true, it.useLabel, "Use label has been set to true")
                    }
                }

            }
        }
    }

}