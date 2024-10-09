package net.nemerosa.ontrack.extensions.environments

import net.nemerosa.ontrack.extensions.environments.rules.core.ManualApprovalSlotAdmissionRule
import net.nemerosa.ontrack.extensions.environments.rules.core.ManualApprovalSlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.rules.core.ManualApprovalSlotAdmissionRuleData
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.extensions.environments.service.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ManualApprovalIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var slotTestSupport: SlotTestSupport

    @Autowired
    private lateinit var slotService: SlotService

    @Test
    fun `Approval by a super admin`() {
        withManuallyApprovedPipeline { pipeline, admissionRuleConfig ->
            asAdmin {
                slotService.approve(pipeline, admissionRuleConfig)
                val status = slotService.startDeployment(
                    pipeline,
                    dryRun = true
                )
                assertTrue(status.status, "Deployment accepted")
                val check = status.checks.first()
                assertTrue(check.status, "Manual approval OK")
                assertNull(check.override, "Manual approval not overridden")

                val state =
                    slotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(pipeline, admissionRuleConfig.id)
                assertNotNull(state, "Manual approval state stored") {
                    assertEquals("admin", it.user)
                    assertEquals(
                        ManualApprovalSlotAdmissionRuleData(
                            approval = true,
                            message = "With pleasure"
                        ),
                        it.data?.parse()
                    )
                }
            }
        }
    }

    private fun SlotService.approve(pipeline: SlotPipeline, admissionRuleConfig: SlotAdmissionRuleConfig) {
        setupAdmissionRule(
            pipeline,
            admissionRuleConfig,
            ManualApprovalSlotAdmissionRuleData(
                approval = true,
                message = "With pleasure"
            ).asJson()
        )
    }

    private fun withManuallyApprovedPipeline(
        code: (pipeline: SlotPipeline, admissionRuleConfig: SlotAdmissionRuleConfig) -> Unit,
    ) {
        slotTestSupport.withSlotPipeline { pipeline ->
            val ruleConfig = ManualApprovalSlotAdmissionRuleConfig(
                message = "Approval required",
            )
            val admissionRuleConfig = SlotAdmissionRuleConfig(
                name = "Manual approval",
                description = "Manual approval is required",
                ruleId = ManualApprovalSlotAdmissionRule.ID,
                ruleConfig = ruleConfig.asJson()
            )
            slotService.addAdmissionRuleConfig(
                pipeline.slot,
                admissionRuleConfig
            )

            code(pipeline, admissionRuleConfig)
        }
    }
}