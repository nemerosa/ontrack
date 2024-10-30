package net.nemerosa.ontrack.extension.environments.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
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

    @Test
    fun `Checking if a pipeline can be deployed when there is no rule`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            run(
                """
                    {
                        slotPipelineById(id: "${pipeline.id}") {
                            deploymentStatus {
                                status
                                override
                                checks {
                                    check {
                                        status
                                        reason
                                    }
                                    ruleId
                                    ruleConfig
                                    ruleData
                                    override {
                                        timestamp
                                        user
                                        override
                                        overrideMessage
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->
                assertEquals(
                    mapOf(
                        "slotPipelineById" to mapOf(
                            "deploymentStatus" to mapOf(
                                "status" to true,
                                "override" to false,
                                "checks" to emptyList<JsonNode>()
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Checking if a pipeline can be deployed with one negative rule`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            /* val pl = */ pipeline.build.branch.promotionLevel("GOLD")
            slotService.addAdmissionRuleConfig(
                config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(pipeline.slot),
            )
            run(
                """
                    {
                        slotPipelineById(id: "${pipeline.id}") {
                            deploymentStatus {
                                status
                                override
                                checks {
                                    check {
                                        status
                                        reason
                                    }
                                    ruleId
                                    ruleConfig
                                    ruleData
                                    override {
                                        timestamp
                                        user
                                        override
                                        overrideMessage
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->
                assertEquals(
                    mapOf(
                        "slotPipelineById" to mapOf(
                            "deploymentStatus" to mapOf(
                                "status" to false,
                                "override" to false,
                                "checks" to listOf(
                                    mapOf(
                                        "check" to mapOf(
                                            "status" to false,
                                            "reason" to "Build not promoted"
                                        ),
                                        "ruleId" to "promotion",
                                        "ruleConfig" to mapOf(
                                            "promotion" to "GOLD"
                                        ),
                                        "ruleData" to null,
                                        "override" to null,
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Checking if a pipeline can be deployed with one positive rule`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            val pl = pipeline.build.branch.promotionLevel("GOLD")
            slotService.addAdmissionRuleConfig(
                config = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(pipeline.slot),
            )
            pipeline.build.promote(pl)
            run(
                """
                    {
                        slotPipelineById(id: "${pipeline.id}") {
                            deploymentStatus {
                                status
                                override
                                checks {
                                    check {
                                        status
                                        reason
                                    }
                                    ruleId
                                    ruleConfig
                                    ruleData
                                    override {
                                        timestamp
                                        user
                                        override
                                        overrideMessage
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->
                assertEquals(
                    mapOf(
                        "slotPipelineById" to mapOf(
                            "deploymentStatus" to mapOf(
                                "status" to true,
                                "override" to false,
                                "checks" to listOf(
                                    mapOf(
                                        "check" to mapOf(
                                            "status" to true,
                                            "reason" to "Build promoted"
                                        ),
                                        "ruleId" to "promotion",
                                        "ruleConfig" to mapOf(
                                            "promotion" to "GOLD"
                                        ),
                                        "ruleData" to null,
                                        "override" to null,
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

    @Test
    fun `Checking if a pipeline can be deployed with one overridden rule`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            /* val pl = */ pipeline.build.branch.promotionLevel("GOLD")
            val admissionRuleConfig = SlotAdmissionRuleTestFixtures.testPromotionAdmissionRuleConfig(pipeline.slot)
            slotService.addAdmissionRuleConfig(
                config = admissionRuleConfig,
            )
            slotService.overrideAdmissionRule(
                pipeline = pipeline,
                admissionRuleConfig = admissionRuleConfig,
                message = "Because I want to",
            )
            run(
                """
                    {
                        slotPipelineById(id: "${pipeline.id}") {
                            deploymentStatus {
                                status
                                override
                                checks {
                                    check {
                                        status
                                        reason
                                    }
                                    ruleId
                                    ruleConfig
                                    ruleData
                                    override {
                                        user
                                        override
                                        overrideMessage
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()
            ) { data ->
                assertEquals(
                    mapOf(
                        "slotPipelineById" to mapOf(
                            "deploymentStatus" to mapOf(
                                "status" to true,
                                "override" to true,
                                "checks" to listOf(
                                    mapOf(
                                        "check" to mapOf(
                                            "status" to false,
                                            "reason" to "Build not promoted"
                                        ),
                                        "ruleId" to "promotion",
                                        "ruleConfig" to mapOf(
                                            "promotion" to "GOLD"
                                        ),
                                        "ruleData" to null,
                                        "override" to mapOf(
                                            "user" to "admin",
                                            "override" to true,
                                            "overrideMessage" to "Because I want to"
                                        ),
                                    )
                                )
                            )
                        )
                    ).asJson(),
                    data
                )
            }
        }
    }

}