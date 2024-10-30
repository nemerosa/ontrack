package net.nemerosa.ontrack.extension.environments.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleTestFixtures
import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.extension.environments.SlotTestSupport
import net.nemerosa.ontrack.extension.environments.rules.core.ManualApprovalSlotAdmissionRuleData
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
                                        number
                                    }
                                    errors {
                                        message
                                    }
                                }
                            }
                        """
                    ) { data ->
                        checkGraphQLUserErrors(data, "startSlotPipeline") { node ->
                            val pipeline = node.path("pipeline")
                            assertEquals(
                                1,
                                pipeline.path("number").asInt()
                            )
                            val pipelineId = pipeline.path("id").asText()
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
                                    config {
                                        ruleId
                                        ruleConfig
                                    }
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
                                    config {
                                        ruleId
                                        ruleConfig
                                    }
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
                                        "config" to mapOf(
                                            "ruleId" to "promotion",
                                            "ruleConfig" to mapOf(
                                                "promotion" to "GOLD"
                                            ),
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
                                    config {
                                        ruleId
                                        ruleConfig
                                    }
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
                                        "config" to mapOf(
                                            "ruleId" to "promotion",
                                            "ruleConfig" to mapOf(
                                                "promotion" to "GOLD"
                                            ),
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
                                    config {
                                        ruleId
                                        ruleConfig
                                    }
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
                                        "config" to mapOf(
                                            "ruleId" to "promotion",
                                            "ruleConfig" to mapOf(
                                                "promotion" to "GOLD"
                                            ),
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

    @Test
    fun `Getting a list of needed inputs for a pipeline`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            val config = SlotAdmissionRuleTestFixtures.testManualApprovalRuleConfig(pipeline.slot)
            slotService.addAdmissionRuleConfig(
                config
            )
            run(
                """
                {
                    slotPipelineById(id: "${pipeline.id}") {
                        requiredInputs {
                            config {
                                id
                                name
                                description
                                ruleId
                                ruleConfig
                            }
                            fields {
                                type
                                name
                                label
                                value
                            }
                        }
                    }
                }
            """.trimIndent()
            ) { data ->
                assertEquals(
                    mapOf(
                        "slotPipelineById" to mapOf(
                            "requiredInputs" to listOf(
                                mapOf(
                                    "config" to mapOf(
                                        "id" to config.id,
                                        "name" to config.name,
                                        "description" to config.description,
                                        "ruleId" to config.ruleId,
                                        "ruleConfig" to config.ruleConfig,
                                    ),
                                    "fields" to listOf(
                                        mapOf(
                                            "type" to "BOOLEAN",
                                            "name" to "approval",
                                            "label" to "Approval",
                                            "value" to null
                                        ),
                                        mapOf(
                                            "type" to "TEXT",
                                            "name" to "message",
                                            "label" to "Approval message",
                                            "value" to null
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

    @Test
    fun `Updating the data for a pipeline`() {
        slotTestSupport.withSlotPipeline { pipeline ->
            val config = SlotAdmissionRuleTestFixtures.testManualApprovalRuleConfig(pipeline.slot)
            slotService.addAdmissionRuleConfig(config)

            assertFalse(slotService.getRequiredInputs(pipeline).isEmpty(), "Pipeline requires some input")

            run(
                """
                    mutation UpdatePipelineData(
                        ${'$'}pipelineId: String!,
                        ${'$'}inputs: [SlotPipelineDataInput!]!,
                    ) {
                        updatePipelineData(input: {
                            pipelineId: ${'$'}pipelineId,
                            inputs: ${'$'}inputs,
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                """.trimIndent(),
                mapOf(
                    "pipelineId" to pipeline.id,
                    "inputs" to listOf(
                        mapOf(
                            "name" to "manualApproval",
                            "values" to listOf(
                                mapOf(
                                    "name" to "approval",
                                    "value" to true,
                                ),
                                mapOf(
                                    "name" to "message",
                                    "value" to "\"OK for me\"",
                                ),
                            )
                        )
                    )
                )
            ) { data ->
                checkGraphQLUserErrors(data, "updatePipelineData")
            }

            assertTrue(slotService.getRequiredInputs(pipeline).isEmpty(), "Pipeline doesn't require inputs any longer")

            val ruleStatus =
                slotService.getPipelineAdmissionRuleStatuses(pipeline)
                    .find { it.admissionRuleConfig.id == config.id }

            assertNotNull(ruleStatus, "Rule status data") {
                val data = it.data?.parse<ManualApprovalSlotAdmissionRuleData>()
                assertEquals(
                    ManualApprovalSlotAdmissionRuleData(
                        approval = true,
                        message = "OK for me"
                    ),
                    data
                )
            }
        }
    }

}