package net.nemerosa.ontrack.extension.environments.rules.core

import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.environments.*
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ManualInputSlotAdmissionRuleIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Not deployable if missing data`() {
        withManualInput { pipeline, _ ->
            val status = slotService.startDeployment(pipeline, dryRun = true)
            assertFalse(status.status, "Pipeline not deployable")
        }
    }

    @Test
    fun `Deployable if having data`() {
        withManualInput { pipeline, config ->
            slotService.updatePipelineData(
                pipeline = pipeline,
                inputs = listOf(
                    SlotPipelineDataInput(
                        name = config.name,
                        values = listOf(
                            SlotPipelineDataInputValue(
                                name = "ticket",
                                value = TextNode("OT-123"),
                            )
                        )
                    )
                )
            )

            val status = slotService.startDeployment(pipeline, dryRun = true)
            assertTrue(status.status, "Pipeline deployable")

            val check = status.checks.find { it.config.id == config.id }
            assertNotNull(check) { ck ->
                val data = ck.ruleData?.parse<ManualInputSlotAdmissionRuleData>()
                assertEquals(
                    ManualInputSlotAdmissionRuleData(
                        items = listOf(
                            SlotPipelineDataInputValue(
                                name = "ticket",
                                value = TextNode("OT-123")
                            )
                        )
                    ),
                    data
                )
            }
        }
    }

    private fun withManualInput(
        code: (
            pipeline: SlotPipeline,
            config: SlotAdmissionRuleConfig,
        ) -> Unit,
    ) {
        slotTestSupport.withSlotPipeline { pipeline ->
            val config = SlotAdmissionRuleConfig(
                slot = pipeline.slot,
                ruleId = ManualInputSlotAdmissionRule.ID,
                ruleConfig = ManualInputSlotAdmissionRuleConfig(
                    fields = listOf(
                        SlotAdmissionRuleInputField(
                            type = SlotAdmissionRuleInputFieldType.TEXT,
                            name = "ticket",
                            label = "Support ticket key",
                            value = null,
                        ),
                    )
                ).asJson(),
                description = null,
            )
            slotService.addAdmissionRuleConfig(config)

            code(pipeline, config)
        }
    }

}