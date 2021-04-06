package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class StalePropertyMutationProviderIT : AbstractQLKTITSupport() {

    @Test
    fun `Setting minimal set of fields`() {
        project {
            run("""
                mutation {
                    setProjectStalePropertyById(input: {
                        id: $id,
                        disablingDuration: 30
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """).let { data ->
                assertNoUserError(data, "setProjectStalePropertyById")

                assertNotNull(getProperty(this, StalePropertyType::class.java), "Property is set") { property ->
                    assertEquals(30, property.disablingDuration)
                    assertEquals(null, property.deletingDuration)
                    assertEquals(null, property.promotionsToKeep)
                    assertEquals(null, property.includes)
                    assertEquals(null, property.excludes)
                }
            }
        }
    }

    @Test
    fun `Setting all fields`() {
        project {
            run("""
                mutation {
                    setProjectStalePropertyById(input: {
                        id: $id,
                        disablingDuration: 30,
                        deletingDuration: 360,
                        promotionsToKeep: [ "PLATINUM" ],
                        includes: "release-.*",
                        excludes: "release-1.*"
                    }) {
                        errors {
                            message
                        }
                    }
                }
            """).let { data ->
                assertNoUserError(data, "setProjectStalePropertyById")

                assertNotNull(getProperty(this, StalePropertyType::class.java), "Property is set") { property ->
                    assertEquals(30, property.disablingDuration)
                    assertEquals(360, property.deletingDuration)
                    assertEquals(listOf("PLATINUM"), property.promotionsToKeep)
                    assertEquals("release-.*", property.includes)
                    assertEquals("release-1.*", property.excludes)
                }
            }
        }
    }

}