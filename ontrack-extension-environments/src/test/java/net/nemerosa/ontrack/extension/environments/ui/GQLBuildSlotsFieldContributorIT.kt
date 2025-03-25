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

    @Test
    fun `List of slots for a build filtered by environment`() {
        slotTestSupport.withSlot { slot1 ->
            /*val slot2 = */ slotTestSupport.slot(project = slot1.project) // Another environment
            /*val slot3 = */ slotTestSupport.slot(environment = slot1.environment) // Another project
            slot1.project.branch {
                build {
                    run(
                        """
                            {
                                build(id: $id) {
                                    slots(environment: "${slot1.environment.name}") {
                                        id
                                    }
                                }
                            }
                        """.trimIndent()
                    ) { data ->
                        val slotId = data.path("build").path("slots")
                            .single().path("id").asText()
                        assertEquals(
                            slot1.id,
                            slotId
                        )
                    }
                }
            }
        }
    }

}