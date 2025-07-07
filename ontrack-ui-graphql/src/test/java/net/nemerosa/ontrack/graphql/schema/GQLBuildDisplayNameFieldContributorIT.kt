package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.extension.general.ReleaseProperty
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GQLBuildDisplayNameFieldContributorIT : AbstractQLKTITSupport() {

    @Test
    fun `Build display name without display name is the build name`() {
        asAdmin {
            project {
                branch {
                    build {
                        run(
                            """
                                {
                                    build(id: $id) {
                                        displayName
                                    }
                                }
                            """.trimIndent()
                        ) { data ->
                            val name = data.path("build").path("displayName").asText()
                            assertEquals(this.name, name)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Build display name`() {
        asAdmin {
            project {
                branch {
                    build {
                        propertyService.editProperty(
                            this,
                            ReleasePropertyType::class.java,
                            ReleaseProperty("1.0.0")
                        )
                        run(
                            """
                                {
                                    build(id: $id) {
                                        displayName
                                    }
                                }
                            """.trimIndent()
                        ) { data ->
                            val name = data.path("build").path("displayName").asText()
                            assertEquals("1.0.0", name)
                        }
                    }
                }
            }
        }
    }

}