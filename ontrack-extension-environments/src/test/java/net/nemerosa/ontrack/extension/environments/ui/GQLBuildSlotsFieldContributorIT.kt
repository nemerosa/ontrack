package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLBuildSlotsFieldContributorIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Test
    fun `List of slots for a build`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                build {
                    run(
                        """
                            {
                                build(id: $id) {
                                    slots {
                                        id
                                    }
                                }
                            }
                        """.trimIndent()
                    ) { data ->
                        val slotId = data.path("build").path("slots")
                            .single().path("id").asText()
                        assertEquals(
                            slot.id,
                            slotId
                        )
                    }
                }
            }
        }
    }

}