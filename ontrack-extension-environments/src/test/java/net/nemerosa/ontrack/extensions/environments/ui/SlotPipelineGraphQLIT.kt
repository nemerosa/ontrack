package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extensions.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extensions.environments.SlotTestSupport
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SlotPipelineGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Starting a pipeline`() {
        slotTestSupport.withSlot { slot ->
            slot.project.branch {
                build {
                    run(
                        """
                            mutation {
                                startSlotPipeline(input: {
                                    slotId: "${slot.id}",
                                    buildId: $id,
                                }) {
                                    pipeline {
                                        id
                                    }
                                    errors {
                                        message
                                    }
                                }
                            }
                        """
                    ) { data ->
                        checkGraphQLUserErrors(data, "startSlotPipeline") { node ->
                            val pipelineId = node
                                .path("pipeline")
                                .path("id")
                                .asText()
                            assertNotNull(slotService.findPipelineById(pipelineId), "Pipeline found") { pipeline ->
                                assertEquals(
                                    SlotPipelineStatus.ONGOING,
                                    pipeline.status
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Getting the current pipeline for a slot`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            run(
                """
                {
                    slotById(id: "${pipeline.slot.id}") {
                        currentPipeline {
                            id
                        }
                    }
                }
            """
            ) { data ->
                assertEquals(
                    pipeline.id,
                    data.path("slotById")
                        .path("currentPipeline")
                        .path("id")
                        .asText()
                )
            }
        }
    }

}